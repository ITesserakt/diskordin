package ru.tesserakt.diskordin.gateway

import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import org.koin.core.KoinComponent
import org.koin.core.get
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.gateway.json.*
import ru.tesserakt.diskordin.gateway.json.commands.Identify
import ru.tesserakt.diskordin.gateway.json.events.HeartbeatACK
import ru.tesserakt.diskordin.gateway.json.events.Hello
import ru.tesserakt.diskordin.util.Loggers
import ru.tesserakt.diskordin.util.toJson
import sun.awt.OSInfo
import java.time.Duration
import java.time.Instant
import kotlin.time.ExperimentalTime

class GatewayProcessor(private val scope: CoroutineScope) : KoinComponent {
    private val logger by Loggers
    private val client = get<IDiscordClient>()
    private var lastHeartbeat: Pair<Instant, Boolean> = Instant.MIN to true

    @ExperimentalTime
    @FlowPreview
    suspend fun answer(
        it: IRawEvent,
        outgoing: SendChannel<Frame>
    ) = when (it) {
        is Hello -> {
            scope.heartbeat(it, outgoing)
            Identify(
                client.token,
                Identify.ConnectionProperties(
                    OSInfo.getOSType().name.toLowerCase(),
                    "diskordin",
                    "diskordin"
                )
            ).wrapWith(Opcode.IDENTIFY, lastSequence).sendTo(outgoing)
        }
        is HeartbeatACK -> lastHeartbeat = lastHeartbeat.copy(second = true)
        else -> logger.warn(it.toString())
    }

    private suspend fun Payload.sendTo(channel: SendChannel<Frame>) = channel.send(Frame.Text(toJson()))

    @FlowPreview
    @ExperimentalTime
    private fun CoroutineScope.heartbeat(
        event: Hello,
        sendChannel: SendChannel<Frame>
    ) = launch(Dispatchers.Unconfined) {
        while (isActive) {
            if (lastHeartbeat.first != Instant.MIN
                && Duration.between(lastHeartbeat.first, Instant.now()) < Duration.ofSeconds(4)
                && !lastHeartbeat.second
            ) client.gateway.restart()

            Heartbeat(lastSequence).wrapWith(Opcode.HEARTBEAT, lastSequence).sendTo(sendChannel)
            logger.debug("Send heartbeat after ${event.heartbeatInterval}ms")
            delay(event.heartbeatInterval)
        }
    }
}
