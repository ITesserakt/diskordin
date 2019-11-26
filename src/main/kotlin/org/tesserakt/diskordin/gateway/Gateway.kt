package org.tesserakt.diskordin.gateway

import arrow.Kind
import arrow.free.fix
import arrow.fx.typeclasses.Async
import arrow.typeclasses.FunctorFilter
import com.tinder.scarlet.Message
import com.tinder.scarlet.websocket.WebSocketEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.tesserakt.diskordin.gateway.json.IPayload
import org.tesserakt.diskordin.gateway.json.IRawEvent
import org.tesserakt.diskordin.gateway.json.Payload
import org.tesserakt.diskordin.gateway.json.token.ConnectionClosed
import org.tesserakt.diskordin.gateway.json.token.ConnectionClosing
import org.tesserakt.diskordin.gateway.json.token.ConnectionFailed
import org.tesserakt.diskordin.gateway.json.token.ConnectionOpened
import org.tesserakt.diskordin.util.fromJson
import org.tesserakt.diskordin.util.toJsonTree
import java.util.concurrent.CancellationException
import kotlin.coroutines.CoroutineContext
import kotlin.time.ExperimentalTime

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE", "unused")
@ExperimentalCoroutinesApi
class Gateway<F> @ExperimentalTime constructor(
    A: Async<F>, private val FF: FunctorFilter<F>
) : KoinComponent, Async<F> by A {
    internal val coroutineContext: CoroutineContext =
        getKoin().getProperty<CoroutineScope>("gatewayScope")!!.coroutineContext

    private val logger = KotlinLogging.logger { }
    private val lifecycle by inject<GatewayLifecycle>()

    internal fun stop() {
        lifecycle.stop()
        coroutineContext.cancel(CancellationException("Shutdown"))
    }

    internal fun restart() {
        lifecycle.restart()
        lifecycle.start()
    }

    @ExperimentalStdlibApi
    private fun FunctorFilter<F>.run(): GatewayAPI<Kind<F, Payload<out IPayload>>> = GatewayAPIF.fx.monad {
        val fromConnection = observeWebSocketEvents<F>().bind().continueOn(coroutineContext)

        fun parseMessage(message: Message) = when (message) {
            is Message.Text -> message.value
            is Message.Bytes -> message.value.decodeToString()
        }.fromJson<Payload<IRawEvent>>()

        fun connectionMapping(state: WebSocketEvent) = when (state) {
            is WebSocketEvent.OnConnectionOpened -> Payload<ConnectionOpened>(
                -1,
                null,
                "CONNECTION_OPENED",
                ConnectionOpened(state.okHttpResponse).toJsonTree()
            )
            is WebSocketEvent.OnMessageReceived -> parseMessage(state.message)
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

        fromConnection.map(::connectionMapping)
    }.fix()

    @ExperimentalStdlibApi
    internal fun run() = FF.run()
}