package org.tesserakt.diskordin.impl.gateway.interceptor

import kotlinx.coroutines.ExperimentalCoroutinesApi
import mu.KotlinLogging
import org.tesserakt.diskordin.core.data.event.lifecycle.HeartbeatACKEvent
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor
import java.time.Instant

class HeartbeatACKInterceptor : EventInterceptor() {
    @ExperimentalCoroutinesApi
    override suspend fun Context.heartbeatACK(event: HeartbeatACKEvent) {
        val logger = KotlinLogging.logger("[Shard #${shard.shardData.index}]")

        shard._heartbeatACKs.value = Instant.now()
        logger.debug("Received heartbeat ACK after ${shard.ping()}ms")
    }
}