package org.tesserakt.diskordin.gateway

import arrow.syntax.function.andThen
import arrow.syntax.function.partially1
import com.tinder.scarlet.Stream
import com.tinder.scarlet.websocket.WebSocketEvent
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.tesserakt.diskordin.gateway.json.Opcode
import org.tesserakt.diskordin.gateway.json.commands.*
import org.tesserakt.diskordin.gateway.json.wrapWith
import org.tesserakt.diskordin.util.toJson

interface GatewayConnection {
    @Send
    fun send(data: String): Boolean

    @Receive
    fun receive(): Stream<WebSocketEvent>
}

suspend fun GatewayConnection.sendPayload(data: GatewayCommand, sequenceId: Int?, shardIndex: Int) =
    withContext(Dispatchers.IO) {
        val logger = KotlinLogging.logger("[Shard #$shardIndex]")

        when (data) {
            is UpdateVoiceState -> data::wrapWith.partially1(Opcode.VOICE_STATUS_UPDATE)
            is RequestGuildMembers -> data::wrapWith.partially1(Opcode.REQUEST_GUILD_MEMBERS)
            is Resume -> data::wrapWith.partially1(Opcode.RESUME)
            is Identify -> data::wrapWith.partially1(Opcode.IDENTIFY)
            is InvalidSession -> data::wrapWith.partially1(Opcode.INVALID_SESSION)
            is Heartbeat -> data::wrapWith.partially1(Opcode.HEARTBEAT)
        } andThen {
            send(it.toJson()).also { result ->
            if (result) logger.debug("---> SENT ${it.opcode()}")
            else logger.debug("-x-> ERROR WHILE SENDING ${it.opcode()}")
        }
    }
}(sequenceId)