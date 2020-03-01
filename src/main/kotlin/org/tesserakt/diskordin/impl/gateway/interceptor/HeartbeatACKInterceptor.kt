package org.tesserakt.diskordin.impl.gateway.interceptor

import arrow.Kind
import arrow.fx.typeclasses.Async
import mu.KotlinLogging
import org.tesserakt.diskordin.core.data.event.lifecycle.HeartbeatACKEvent
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor
import java.time.Instant

class HeartbeatACKInterceptor<F>(private val A: Async<F>) : EventInterceptor<F>(A) {
    override fun Context.heartbeatACK(event: HeartbeatACKEvent): Kind<F, Unit> {
        val logger = KotlinLogging.logger("[Shard #${shard.shardData.current}]")

        shard.lastHeartbeatACK = Instant.now()
        logger.debug("Received heartbeat ACK after ${shard.ping()}ms")

        if (shard.ping() >= 20000) {
            logger.warn("Shard #${shard.shardData.current} does not respond. Closing")
            controller.closeShard(shard.shardData.current)
        }

        return A.unit()
    }
}