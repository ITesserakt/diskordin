package org.tesserakt.diskordin.gateway.transformer

import org.tesserakt.diskordin.gateway.json.IPayload
import org.tesserakt.diskordin.gateway.json.IRawEvent
import org.tesserakt.diskordin.gateway.json.Payload
import org.tesserakt.diskordin.gateway.json.WebSocketEvent
import org.tesserakt.diskordin.gateway.json.token.ConnectionClosed
import org.tesserakt.diskordin.gateway.json.token.ConnectionClosing
import org.tesserakt.diskordin.gateway.json.token.ConnectionFailed
import org.tesserakt.diskordin.gateway.json.token.ConnectionOpened
import org.tesserakt.diskordin.util.fromJson
import org.tesserakt.diskordin.util.toJsonTree

object WebSocketEventTransformer :
    Transformer<WebSocketEvent, Payload<out IPayload>> {
    override fun transform(context: WebSocketEvent) = when (context) {
        is WebSocketEvent.ConnectionOpened -> Payload<ConnectionOpened>(
            -1,
            null,
            "CONNECTION_OPENED",
            ConnectionOpened.toJsonTree()
        )
        is WebSocketEvent.MessageReceived -> context.message.decompress().fromJson<Payload<IRawEvent>>()
        is WebSocketEvent.ConnectionClosing -> Payload<ConnectionClosing>(
            -1,
            null,
            "CONNECTION_CLOSING",
            ConnectionClosing(context.code, context.reason).toJsonTree()
        )
        is WebSocketEvent.ConnectionClosed -> Payload<ConnectionClosed>(
            -1,
            null,
            "CONNECTION_CLOSED",
            ConnectionClosed.toJsonTree()
        )
        is WebSocketEvent.ConnectionFailed -> Payload<ConnectionFailed>(
            -1,
            null,
            "CONNECTION_FAILED",
            ConnectionFailed(context.error).toJsonTree()
        )
    }
}