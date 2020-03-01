package org.tesserakt.diskordin.impl.gateway.interceptor

import arrow.Kind
import arrow.fx.typeclasses.Async
import mu.KotlinLogging
import org.tesserakt.diskordin.core.data.event.lifecycle.InvalidSessionEvent
import org.tesserakt.diskordin.core.data.event.lifecycle.ReadyEvent
import org.tesserakt.diskordin.core.data.event.lifecycle.ResumedEvent
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor
import org.tesserakt.diskordin.gateway.shard.Shard

class ShardApprover<F>(A: Async<F>) : EventInterceptor<F>(A) {
    private val logger = KotlinLogging.logger { }

    override fun Context.ready(event: ReadyEvent): Kind<F, Unit> {
        val (shardIndex, shardCount) = event.shardData
        controller.approveShard(shard, event.sessionId)
        shard.state = Shard.State.Connected

        logger.debug("Shard #${shardIndex + 1}/${shardCount} ready!")
        return unit()
    }

    override fun Context.resumed(event: ResumedEvent): Kind<F, Unit> {
        val (shardIndex, shardCount) = shard.shardData
        controller.approveShard(shard, shard.sessionId)
        shard.state = Shard.State.Connected

        logger.debug("Shard #${shardIndex + 1}/${shardCount} resumed!")
        return unit()
    }

    override fun Context.invalidSession(event: InvalidSessionEvent): Kind<F, Unit> {
        shard.state = Shard.State.Disconnected
        return unit()
    }
}