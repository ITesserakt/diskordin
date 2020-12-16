package org.tesserakt.diskordin.gateway.json

sealed class WebSocketEvent {
    object ConnectionOpened : WebSocketEvent()

    data class MessageReceived(val message: Message) : WebSocketEvent()

    object ConnectionClosed : WebSocketEvent()

    data class ConnectionClosing(val code: Short, val reason: String) : WebSocketEvent()

    data class ConnectionFailed(val error: Throwable) : WebSocketEvent()
}