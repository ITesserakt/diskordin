package org.tesserakt.diskordin.impl.gateway.handler

import arrow.core.FunctionK
import arrow.core.identity
import arrow.fx.typeclasses.Async
import arrow.typeclasses.Monad
import mu.KotlinLogging
import org.tesserakt.diskordin.core.client.EventDispatcher
import org.tesserakt.diskordin.core.client.WebSocketStateHolder
import org.tesserakt.diskordin.core.data.event.lifecycle.HeartbeatACKEvent
import org.tesserakt.diskordin.core.data.event.lifecycle.HeartbeatEvent
import org.tesserakt.diskordin.core.data.event.lifecycle.HelloEvent
import org.tesserakt.diskordin.gateway.ForGatewayAPIF
import org.tesserakt.diskordin.gateway.json.Opcode
import org.tesserakt.diskordin.gateway.json.Payload
import org.tesserakt.diskordin.gateway.json.asInt
import org.tesserakt.diskordin.gateway.json.commands.Heartbeat
import org.tesserakt.diskordin.gateway.json.token.ConnectionFailed
import org.tesserakt.diskordin.gateway.sendPayload
import org.tesserakt.diskordin.util.toJsonTree
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds
import kotlin.time.seconds

private var lastHeartbeat: Instant? = null
private val logger = KotlinLogging.logger("[Gateway]")

fun <F> EventDispatcher<F>.heartbeatHandler(
    sequenceId: () -> Int?,
    compiler: FunctionK<ForGatewayAPIF, F>,
    A: Async<F>
) = A.fx.async {
    val heartbeat = !subscribeOn<HeartbeatEvent>()
    !sendPayload(Heartbeat(heartbeat.v), sequenceId()).foldMap(compiler, this)
    Unit
}

fun <F> EventDispatcher<F>.getInterval(M: Monad<F>) = M.run {
    subscribeOn<HelloEvent>().map { it.heartbeatInterval }
}

@ExperimentalTime
fun <F> EventDispatcher<F>.heartbeatACKHandler(
    state: WebSocketStateHolder,
    A: Async<F>
) = A.fx.async {
    val interval = !getInterval(this)
    val (_) = subscribeOn<HeartbeatACKEvent>()
    val now = !effect { Instant.now() }
    val diff = lastHeartbeat
        ?.toEpochMilli()
        ?.minus(now.toEpochMilli())
        ?.plus(interval)
        ?.let { 0 - it }
        ?.milliseconds
        ?: Duration.ZERO

    lastHeartbeat = now

    val sign = when {
        diff.isPositive() -> "+"
        else -> ""
    }
    logger.debug("Received heartbeat ACK after ${interval + diff.toLongMilliseconds()}ms ($sign${diff.toLongMilliseconds()}ms)")

    if (diff > 20.seconds) {
        logger.error("Gateway does not responding. Attempt to restart")
        !state.update(
            Payload(
                Opcode.UNDERLYING.asInt(),
                null,
                "CONNECTION_FAILED",
                ConnectionFailed(IllegalStateException("Restart")).toJsonTree()
            )
        ).fromEither(::identity)
    }
}