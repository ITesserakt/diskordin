package org.tesserakt.diskordin.gateway.interpreter

import com.tinder.scarlet.Stream
import com.tinder.scarlet.websocket.WebSocketEvent
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import org.tesserakt.diskordin.gateway.json.Payload
import org.tesserakt.diskordin.gateway.json.commands.GatewayCommand

interface Implementation {
    @Send
    fun send(data: Payload<out GatewayCommand>): Boolean

    @Receive
    fun receive(): Stream<WebSocketEvent>
}