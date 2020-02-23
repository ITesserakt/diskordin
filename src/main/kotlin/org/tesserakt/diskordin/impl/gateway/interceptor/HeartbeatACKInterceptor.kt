package org.tesserakt.diskordin.impl.gateway.interceptor

import mu.KotlinLogging
import org.tesserakt.diskordin.core.data.event.lifecycle.HeartbeatACKEvent
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor
import java.time.Instant

class HeartbeatACKInterceptor : EventInterceptor() {
    override suspend fun Context.heartbeatACK(event: HeartbeatACKEvent) {
        val logger = KotlinLogging.logger("[Shard #${shard.shardData.current}]")

        shard.lastHeartbeatACK = Instant.now()
        logger.debug("Received heartbeat ACK after ${shard.ping()}ms")

        if (shard.ping() >= 20000) {
            logger.warn("Shard #${shard.shardData.current} does not respond. Closing")
            controller.closeShard(shard.shardData.current)
        }
    }
}