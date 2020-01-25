package org.tesserakt.diskordin.gateway.interceptor

import com.tinder.scarlet.Message
import com.tinder.scarlet.websocket.WebSocketEvent
import org.tesserakt.diskordin.gateway.json.IPayload
import org.tesserakt.diskordin.gateway.json.IRawEvent
import org.tesserakt.diskordin.gateway.json.Payload
import org.tesserakt.diskordin.gateway.json.token.ConnectionClosed
import org.tesserakt.diskordin.gateway.json.token.ConnectionClosing
import org.tesserakt.diskordin.gateway.json.token.ConnectionFailed
import org.tesserakt.diskordin.gateway.json.token.ConnectionOpened
import org.tesserakt.diskordin.util.fromJson
import org.tesserakt.diskordin.util.toJsonTree

object WebSocketEventTransformer : Transformer<WebSocketEvent, Payload<out IPayload>> {
    @UseExperimental(ExperimentalStdlibApi::class)
    override fun transform(context: WebSocketEvent) = when (context) {
        is WebSocketEvent.OnConnectionOpened -> Payload<ConnectionOpened>(
            -1,
            null,
            "CONNECTION_OPENED",
            ConnectionOpened.toJsonTree()
        )
        is WebSocketEvent.OnMessageReceived -> when (val ms = context.message) {
            is Message.Text -> ms.value
            is Message.Bytes -> ms.value.decodeToString()
        }.fromJson<Payload<IRawEvent>>()
        is WebSocketEvent.OnConnectionClosing -> Payload<ConnectionClosing>(
            -1,
            null,
            "CONNECTION_CLOSING",
            ConnectionClosing(context.shutdownReason).toJsonTree()
        )
        is WebSocketEvent.OnConnectionClosed -> Payload<ConnectionClosed>(
            -1,
            null,
            "CONNECTION_CLOSED",
            ConnectionClosed(context.shutdownReason).toJsonTree()
        )
        is WebSocketEvent.OnConnectionFailed -> Payload<ConnectionFailed>(
            -1,
            null,
            "CONNECTION_FAILED",
            ConnectionFailed(context.throwable).toJsonTree()
        )
    }
}