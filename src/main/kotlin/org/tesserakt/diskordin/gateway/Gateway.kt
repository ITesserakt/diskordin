package org.tesserakt.diskordin.gateway

import arrow.Kind
import arrow.core.FunctionK
import arrow.core.toT
import arrow.fx.typeclasses.Async
import com.tinder.scarlet.Message
import com.tinder.scarlet.websocket.WebSocketEvent
import okhttp3.OkHttpClient
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.core.client.IGatewayLifecycleManager
import org.tesserakt.diskordin.gateway.json.IPayload
import org.tesserakt.diskordin.gateway.json.IRawEvent
import org.tesserakt.diskordin.gateway.json.Payload
import org.tesserakt.diskordin.gateway.json.token.ConnectionClosed
import org.tesserakt.diskordin.gateway.json.token.ConnectionClosing
import org.tesserakt.diskordin.gateway.json.token.ConnectionFailed
import org.tesserakt.diskordin.gateway.json.token.ConnectionOpened
import org.tesserakt.diskordin.impl.core.client.setupScarlet
import org.tesserakt.diskordin.util.fromJson
import org.tesserakt.diskordin.util.toJsonTree
import kotlin.coroutines.CoroutineContext

typealias GatewayCompiler<G> = FunctionK<ForGatewayAPIF, G>

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE", "unused")
class Gateway(private val scheduler: CoroutineContext, private val lifecycleRegistry: IGatewayLifecycleManager) {
    var sequenceId: Int? = null
        private set

    @ExperimentalStdlibApi
    internal fun <G> run(compiler: GatewayCompiler<G>, A: Async<G>): Kind<G, Payload<out IPayload>> {
        lifecycleRegistry.start()
        val fromConnection = observeWebSocketEvents()

        fun parseMessage(message: Message) = when (message) {
            is Message.Text -> message.value
            is Message.Bytes -> message.value.decodeToString()
        }.fromJson<Payload<IRawEvent>>()

        fun connectionMapping(state: WebSocketEvent) = when (state) {
            is WebSocketEvent.OnConnectionOpened -> Payload<ConnectionOpened>(
                -1,
                null,
                "CONNECTION_OPENED",
                ConnectionOpened.toJsonTree()
            )
            is WebSocketEvent.OnMessageReceived -> parseMessage(state.message).also {
                sequenceId = it.seq ?: sequenceId
            }
            is WebSocketEvent.OnConnectionClosing -> Payload<ConnectionClosing>(
                -1,
                null,
                "CONNECTION_CLOSING",
                ConnectionClosing(state.shutdownReason).toJsonTree()
            )
            is WebSocketEvent.OnConnectionClosed -> Payload<ConnectionClosed>(
                -1,
                null,
                "CONNECTION_CLOSED",
                ConnectionClosed(state.shutdownReason).toJsonTree()
            )
            is WebSocketEvent.OnConnectionFailed -> Payload<ConnectionFailed>(
                -1,
                null,
                "CONNECTION_FAILED",
                ConnectionFailed(state.throwable).toJsonTree()
            )
        }

        val transformed = fromConnection.map(::connectionMapping).compile(compiler).fold(A)
        A.run {
            return transformed.continueOn(scheduler)
        }
    }

    companion object Factory {
        private const val gatewayVersion = 6
        private const val encoding = "json"

        private val gatewayUrl = { start: String, compression: String ->
            "$start/?v=$gatewayVersion&encoding=$encoding&compression=$compression"
        }

        private val scarlet = { start: String, compression: String, httpClient: OkHttpClient ->
            setupScarlet(gatewayUrl(start, compression), httpClient)
        }

        private val impl = { start: String, compression: String, httpClient: OkHttpClient ->
            scarlet(start, compression, httpClient).create<Implementation>()
        }

        fun create(context: BootstrapContext.Gateway) =
            Gateway(context.scheduler, context.lifecycleRegistry) toT impl(
                context.connectionContext.url,
                context.connectionContext.compression,
                context.httpClient.memoize().extract()
            )
    }
}