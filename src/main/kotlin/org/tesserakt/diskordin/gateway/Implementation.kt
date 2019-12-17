package org.tesserakt.diskordin.gateway

import com.tinder.scarlet.Stream
import com.tinder.scarlet.websocket.WebSocketEvent
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send

interface Implementation {
    @Send
    fun send(data: String): Boolean

    @Receive
    fun receive(): Stream<WebSocketEvent>
}