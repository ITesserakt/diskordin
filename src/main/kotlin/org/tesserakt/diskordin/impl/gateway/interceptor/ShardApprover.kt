package org.tesserakt.diskordin.impl.gateway.interceptor

import mu.KotlinLogging
import org.tesserakt.diskordin.core.data.event.lifecycle.InvalidSessionEvent
import org.tesserakt.diskordin.core.data.event.lifecycle.ReadyEvent
import org.tesserakt.diskordin.core.data.event.lifecycle.ResumedEvent
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor
import org.tesserakt.diskordin.gateway.shard.Shard

class ShardApprover : EventInterceptor() {
    private val logger = KotlinLogging.logger { }

    override suspend fun Context.ready(event: ReadyEvent) {
        val (shardIndex, shardCount) = event.shardData
        controller.approveShard(shard, event.sessionId)
        shard.state = Shard.State.Connected

        logger.debug("Shard #${shardIndex + 1}/${shardCount} ready!")
    }

    override suspend fun Context.resumed(event: ResumedEvent) {
        val (shardIndex, shardCount) = shard.shardData
        controller.approveShard(shard, shard.sessionId)
        shard.state = Shard.State.Connected

        logger.debug("Shard #${shardIndex + 1}/${shardCount} resumed!")
    }

    override suspend fun Context.invalidSession(event: InvalidSessionEvent) {
        shard.state = Shard.State.Disconnected
    }
}