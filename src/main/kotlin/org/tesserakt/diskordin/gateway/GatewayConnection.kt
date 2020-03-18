package org.tesserakt.diskordin.gateway

import arrow.fx.Schedule
import arrow.fx.retry
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.milliseconds
import arrow.syntax.function.partially1
import com.tinder.scarlet.Stream
import com.tinder.scarlet.websocket.WebSocketEvent
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import kotlinx.coroutines.Dispatchers
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

fun <F> GatewayConnection.sendPayload(
    data: GatewayCommand,
    sequenceId: Int?,
    shardIndex: Int,
    CC: Concurrent<F>
) = CC.fx.async {
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

    !effect(Dispatchers.IO) { connection.send(json) }
        .flatMap {
            if (it) logger.debug("---> SENT ${payload.opcode()}").just()
            else raiseError(IllegalStateException("Payload was not send"))
        }.retry(CC, Schedule.exponential<F, Throwable>(CC, 500.milliseconds).jittered(CC))
}