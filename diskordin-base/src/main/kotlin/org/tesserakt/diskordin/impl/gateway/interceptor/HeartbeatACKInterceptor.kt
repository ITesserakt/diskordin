package org.tesserakt.diskordin.impl.gateway.interceptor

import kotlinx.datetime.Clock
import mu.KotlinLogging
import org.tesserakt.diskordin.core.data.event.lifecycle.HeartbeatACKEvent
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor
import kotlin.time.ExperimentalTime

class HeartbeatACKInterceptor : EventInterceptor() {
    @ExperimentalTime
    override suspend fun Context.heartbeatACK(event: HeartbeatACKEvent) {
        val logger = KotlinLogging.logger("[Shard #${shard.shardData.index}]")

        shard._heartbeatACKs.value = Clock.System.now()
        logger.debug("Received heartbeat ACK after ${shard.ping()}")
    }
}