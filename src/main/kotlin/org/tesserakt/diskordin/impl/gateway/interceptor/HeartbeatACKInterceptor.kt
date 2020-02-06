package org.tesserakt.diskordin.impl.gateway.interceptor

import mu.KotlinLogging
import org.tesserakt.diskordin.core.data.event.lifecycle.HeartbeatACKEvent
import org.tesserakt.diskordin.core.data.event.lifecycle.HelloEvent
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor
import java.time.Instant

class HeartbeatACKInterceptor : EventInterceptor() {
    private var interval = 0L
    private var lastHeartbeatTime: Instant = Instant.now()
    private val logger = KotlinLogging.logger("[Gateway]")

    override suspend fun Context.hello(event: HelloEvent) {
        interval = event.heartbeatInterval
    }

    override suspend fun Context.heartbeatACK(event: HeartbeatACKEvent) {
        val timeNow: Instant = Instant.now()
        val ping = timeNow.toEpochMilli() - lastHeartbeatTime.toEpochMilli() - interval
        lastHeartbeatTime = timeNow

        logger.debug("Received heartbeat ACK after ${interval + ping}ms")

        if (ping >= 20000) {
            logger.warn("Gateway does not respond. Attempt to restart")
            //TODO restart process
        }
    }
}