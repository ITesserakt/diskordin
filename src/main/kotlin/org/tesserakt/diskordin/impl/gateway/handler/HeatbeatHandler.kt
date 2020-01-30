package org.tesserakt.diskordin.impl.gateway.handler

import arrow.fx.typeclasses.Async
import mu.KotlinLogging
import org.tesserakt.diskordin.core.client.EventDispatcher
import org.tesserakt.diskordin.core.client.WebSocketStateHolder
import org.tesserakt.diskordin.core.data.event.lifecycle.HeartbeatACKEvent
import org.tesserakt.diskordin.core.data.event.lifecycle.HelloEvent
import org.tesserakt.diskordin.gateway.json.token.ConnectionFailed
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds
import kotlin.time.seconds

private var lastHeartbeat: Instant? = null
private val logger = KotlinLogging.logger("[Gateway]")

@ExperimentalTime
fun <F> EventDispatcher<F>.heartbeatACKHandler(
    state: WebSocketStateHolder,
    A: Async<F>
) = A.fx.async {
    val interval = subscribeOn<HelloEvent>().bind().heartbeatInterval
    !subscribeOn<HeartbeatACKEvent>()
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
        state.update(
            ConnectionFailed(IllegalStateException("Restart"))
        )
    }
}