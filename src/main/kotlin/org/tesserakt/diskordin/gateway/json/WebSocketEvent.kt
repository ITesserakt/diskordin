package org.tesserakt.diskordin.gateway.json

sealed class WebSocketEvent {
    object ConnectionOpened : WebSocketEvent()

    data class MessageReceived(val message: Message) : WebSocketEvent()

    data class ConnectionClosing(val code: Short, val reason: String) : WebSocketEvent()

    data class ConnectionClosed(val code: Short, val reason: String) : WebSocketEvent()

    data class ConnectionFailed(val error: Throwable) : WebSocketEvent()
}