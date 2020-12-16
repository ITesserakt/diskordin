package org.tesserakt.diskordin.gateway

import com.tinder.scarlet.*
import com.tinder.scarlet.utils.getRawType
import com.tinder.scarlet.websocket.ShutdownReason
import com.tinder.scarlet.websocket.okhttp.OkHttpWebSocket
import org.tesserakt.diskordin.gateway.json.WebSocketEvent
import java.lang.reflect.Type

object WebSocketEventAdapter : ProtocolSpecificEventAdapter {
    object Factory : ProtocolSpecificEventAdapter.Factory {
        override fun create(type: Type, annotations: Array<Annotation>): WebSocketEventAdapter {
            val receivingType = type.getRawType()
            require(WebSocketEventWrap::class.java.isAssignableFrom(receivingType)) {
                "Only subclasses of WebSocketEvent are supported"
            }
            return WebSocketEventAdapter
        }
    }

    override fun fromEvent(event: ProtocolEvent): ProtocolSpecificEvent = when (event) {
        is ProtocolEvent.OnOpened -> WebSocketEvent.ConnectionOpened.wrap()
        is ProtocolEvent.OnMessageReceived -> WebSocketEvent.MessageReceived(event.message()).wrap()
        is ProtocolEvent.OnMessageDelivered -> WebSocketEvent.MessageReceived(event.message()).wrap()
        is ProtocolEvent.OnClosing -> event.response.toEvent().wrap()
        is ProtocolEvent.OnClosed -> WebSocketEvent.ConnectionClosed.wrap()
        is ProtocolEvent.OnFailed -> WebSocketEvent.ConnectionFailed(event.throwable ?: Throwable("UNKNOWN")).wrap()
    }

    private fun Protocol.CloseResponse.toEvent(): WebSocketEvent {
        val (code, reason) = (this as? OkHttpWebSocket.CloseResponse)?.shutdownReason ?: ShutdownReason.GRACEFUL
        return WebSocketEvent.ConnectionClosing(code.toShort(), reason)
    }
}

inline class WebSocketEventWrap(val event: WebSocketEvent) : ProtocolSpecificEvent

fun WebSocketEvent.wrap() = WebSocketEventWrap(this)
fun WebSocketEventWrap.unwrap() = event

operator fun Message.invoke() = when (this) {
    is Message.Text -> org.tesserakt.diskordin.gateway.json.Message.Text(this.value)
    is Message.Bytes -> org.tesserakt.diskordin.gateway.json.Message.Bytes(this.value)
}

operator fun org.tesserakt.diskordin.gateway.json.Message.invoke() = when (this) {
    is org.tesserakt.diskordin.gateway.json.Message.Text -> Message.Text(this.message)
    is org.tesserakt.diskordin.gateway.json.Message.Bytes -> Message.Bytes(this.message)
}