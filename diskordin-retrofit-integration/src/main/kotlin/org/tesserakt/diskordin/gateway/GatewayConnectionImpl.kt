package org.tesserakt.diskordin.gateway

import arrow.fx.coroutines.stream.Stream
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import org.tesserakt.diskordin.gateway.json.Message
import org.tesserakt.diskordin.gateway.json.WebSocketEvent

interface GatewayConnectionImpl {
    @Send
    fun send(data: Message)

    @Receive
    fun receive(): Stream<WebSocketEventWrap>
}

fun GatewayConnectionImpl.unwrap() = object : GatewayConnection {
    override fun send(data: Message) = this@unwrap.send(data)
    override fun receive(): Stream<WebSocketEvent> = this@unwrap.receive().map { it.unwrap() }
}