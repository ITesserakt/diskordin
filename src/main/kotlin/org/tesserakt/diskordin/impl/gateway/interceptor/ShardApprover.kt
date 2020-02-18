package org.tesserakt.diskordin.impl.gateway.interceptor

import mu.KotlinLogging
import org.tesserakt.diskordin.core.data.event.lifecycle.ReadyEvent
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor

class ShardApprover : EventInterceptor() {
    private val logger = KotlinLogging.logger { }

    override suspend fun Context.ready(event: ReadyEvent) {
        val (shardIndex, shardCount) = event.shardData
        controller.approveShard(shard, event.sessionId)
        logger.debug("Shard #${shardIndex + 1}/${shardCount} ready!")
    }
}