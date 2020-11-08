package org.tesserakt.diskordin.gateway

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.fx.IO
import arrow.fx.coroutines.ForkConnected
import arrow.fx.coroutines.stream.Stream
import arrow.fx.extensions.io.environment.environment
import arrow.syntax.function.partially1
import mu.KotlinLogging
import org.tesserakt.diskordin.gateway.json.Message
import org.tesserakt.diskordin.gateway.json.Opcode
import org.tesserakt.diskordin.gateway.json.WebSocketEvent
import org.tesserakt.diskordin.gateway.json.commands.*
import org.tesserakt.diskordin.gateway.json.wrapWith
import org.tesserakt.diskordin.util.toJson

interface GatewayConnection {
    fun send(data: Message)
    fun receive(): Stream<WebSocketEvent>
}

suspend fun GatewayConnection.sendPayload(
    data: GatewayCommand,
    sequenceId: Int?,
    shardIndex: Int
) {
    val connection = this@sendPayload
    val logger = KotlinLogging.logger("[Shard #$shardIndex]")

    val payload = when (data) {
        is UpdateVoiceState -> data::wrapWith.partially1(Opcode.VOICE_STATUS_UPDATE)
        is RequestGuildMembers -> data::wrapWith.partially1(Opcode.REQUEST_GUILD_MEMBERS)
        is Resume -> data::wrapWith.partially1(Opcode.RESUME)
        is Identify -> data::wrapWith.partially1(Opcode.IDENTIFY)
        is InvalidSession -> data::wrapWith.partially1(Opcode.INVALID_SESSION)
        is Heartbeat -> data::wrapWith.partially1(Opcode.HEARTBEAT)
    }.invoke(sequenceId)
    val json = payload.toJson()

    ForkConnected(IO.environment().dispatchers().io()) {
        val isSent = Either.catch { connection.send(Message.Text(json)) }.isRight()
        if (isSent) logger.debug("---> SENT ${payload.opcode()}").right()
        else IllegalStateException("Payload was not send").left()
    }
}