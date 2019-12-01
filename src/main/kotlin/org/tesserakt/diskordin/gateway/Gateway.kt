package org.tesserakt.diskordin.gateway

import arrow.core.FunctionK
import arrow.core.toT
import arrow.free.FreeApplicative
import arrow.syntax.function.andThen
import com.tinder.scarlet.Message
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.websocket.WebSocketEvent
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.parameter.parametersOf
import org.tesserakt.diskordin.gateway.json.IPayload
import org.tesserakt.diskordin.gateway.json.IRawEvent
import org.tesserakt.diskordin.gateway.json.Payload
import org.tesserakt.diskordin.gateway.json.token.ConnectionClosed
import org.tesserakt.diskordin.gateway.json.token.ConnectionClosing
import org.tesserakt.diskordin.gateway.json.token.ConnectionFailed
import org.tesserakt.diskordin.gateway.json.token.ConnectionOpened
import org.tesserakt.diskordin.util.fromJson
import org.tesserakt.diskordin.util.toJsonTree

typealias Compiled<G, A> = FreeApplicative<G, A>

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE", "unused")
class Gateway {
    var sequenceId: Int? = null
        private set

    @ExperimentalStdlibApi
    internal fun <G> run(compiler: FunctionK<ForGatewayAPIF, G>): Compiled<G, Payload<out IPayload>> {
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

        return fromConnection.map(::connectionMapping).compile(compiler)
    }

    companion object Factory : KoinComponent {
        private const val gatewayVersion = 6
        private const val encoding = "json"

        private val gatewayUrl = { start: String, compression: String ->
            "$start/?v=$gatewayVersion&encoding=$encoding&compression=$compression"
        }

        private val scarlet = gatewayUrl andThen {
            get<Scarlet> { parametersOf(it) }
        }

        private val impl = scarlet andThen { it.create<Implementation>() }

        fun create(start: String, compression: String) =
            Gateway() toT impl(start, compression)
    }
}