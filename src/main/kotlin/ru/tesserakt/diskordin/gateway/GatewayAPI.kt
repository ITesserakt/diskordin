package ru.tesserakt.diskordin.gateway

import com.tinder.scarlet.websocket.WebSocketEvent
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import kotlinx.coroutines.flow.Flow
import ru.tesserakt.diskordin.gateway.json.IRawEvent
import ru.tesserakt.diskordin.gateway.json.Payload
import ru.tesserakt.diskordin.gateway.json.commands.*

interface GatewayAPI {
    @Send
    fun heartbeat(data: Payload<Heartbeat>): Boolean

    @Send
    fun identify(data: Payload<Identify>): Boolean

    @Receive
    fun observeWebSocketEvents(): Flow<WebSocketEvent>

    @Receive
    fun observeDiscordEvents(): Flow<Payload<IRawEvent>>

    @Send
    fun resume(data: Payload<Resume>): Boolean

    @Send
    fun requestMembers(data: Payload<RequestGuildMembers>): Boolean

    @Send
    fun invalidate(data: Payload<InvalidSession>): Boolean

    @Send
    fun updateVoiceState(data: Payload<UpdateVoiceState>): Boolean
}
