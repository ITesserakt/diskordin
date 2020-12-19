package org.tesserakt.diskordin.gateway

import arrow.syntax.function.partially1
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharedFlow
import org.tesserakt.diskordin.gateway.json.Message
import org.tesserakt.diskordin.gateway.json.Opcode
import org.tesserakt.diskordin.gateway.json.WebSocketEvent
import org.tesserakt.diskordin.gateway.json.commands.*
import org.tesserakt.diskordin.gateway.json.wrapWith
import org.tesserakt.diskordin.util.toJson

interface GatewayConnection {
    suspend fun send(data: Message): Job
    fun receive(): SharedFlow<WebSocketEvent>
}

suspend fun GatewayConnection.sendPayload(
    data: GatewayCommand,
    sequenceId: Int?
): Job {
    val payload = when (data) {
        is UpdateVoiceState -> data::wrapWith.partially1(Opcode.VOICE_STATUS_UPDATE)
        is RequestGuildMembers -> data::wrapWith.partially1(Opcode.REQUEST_GUILD_MEMBERS)
        is Resume -> data::wrapWith.partially1(Opcode.RESUME)
        is Identify -> data::wrapWith.partially1(Opcode.IDENTIFY)
        is InvalidSession -> data::wrapWith.partially1(Opcode.INVALID_SESSION)
        is Heartbeat -> data::wrapWith.partially1(Opcode.HEARTBEAT)
    }.invoke(sequenceId)
    val json = payload.toJson()

    return send(Message.Text(json))
}