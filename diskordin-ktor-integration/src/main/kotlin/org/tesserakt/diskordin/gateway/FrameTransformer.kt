package org.tesserakt.diskordin.gateway

import io.ktor.http.cio.websocket.*
import org.tesserakt.diskordin.gateway.json.Message
import org.tesserakt.diskordin.gateway.json.WebSocketEvent
import org.tesserakt.diskordin.gateway.transformer.Transformer

object FrameTransformer : Transformer<Frame, WebSocketEvent> {
    override fun transform(context: Frame): WebSocketEvent = when (context) {
        is Frame.Close -> context.readReason()?.toEvent() ?: WebSocketEvent.ConnectionClosing(1000, "OK")
        is Frame.Binary -> context.readBytes().toEvent()
        is Frame.Text -> context.readText().toEvent()
        is Frame.Ping -> context.readBytes().toEvent()
        is Frame.Pong -> context.readBytes().toEvent()
    }

    private fun CloseReason.toEvent() = WebSocketEvent.ConnectionClosing(code, message)
    private fun ByteArray.toEvent() = WebSocketEvent.MessageReceived(Message.Bytes(this))
    private fun String.toEvent() = WebSocketEvent.MessageReceived(Message.Text(this))
}