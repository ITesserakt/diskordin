package org.tesserakt.diskordin.gateway

import arrow.free.map
import arrow.fx.typeclasses.Async
import arrow.typeclasses.FunctorFilter
import com.tinder.scarlet.Message
import com.tinder.scarlet.lifecycle.LifecycleRegistry
import com.tinder.scarlet.websocket.WebSocketEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import mu.KotlinLogging
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
) : Async<F> by A {
    private val coroutineContext: CoroutineContext = Dispatchers.IO
    private val logger = KotlinLogging.logger { }
    private val lifecycle = GatewayLifecycle(LifecycleRegistry())

    internal fun stop() {
        lifecycle.stop()
        coroutineContext.cancel(CancellationException("Shutdown"))
    }

    internal fun restart() {
        lifecycle.restart()
        lifecycle.start()
    }

    @ExperimentalStdlibApi
    private fun FunctorFilter<F>.run(): GatewayAPI<Payload<out IPayload>> {
        lifecycle.start()
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

        return fromConnection.map(::connectionMapping)
    }

    @ExperimentalStdlibApi
    internal fun run() = FF.run()
}