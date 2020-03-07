package org.tesserakt.diskordin.impl.gateway.interceptor

import arrow.Kind
import arrow.fx.typeclasses.Concurrent
import mu.KotlinLogging
import org.tesserakt.diskordin.core.data.event.lifecycle.InvalidSessionEvent
import org.tesserakt.diskordin.core.data.event.lifecycle.ReadyEvent
import org.tesserakt.diskordin.core.data.event.lifecycle.ReconnectEvent
import org.tesserakt.diskordin.core.data.event.lifecycle.ResumedEvent
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor
import org.tesserakt.diskordin.gateway.shard.Shard

class ShardApprover<F>(private val CC: Concurrent<F>) : EventInterceptor<F>(CC) {
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

    override fun Context.invalidSession(event: InvalidSessionEvent): Kind<F, Unit> = CC.fx.concurrent {
        shard.state =
            if (event.canResume) Shard.State.Disconnected
            else Shard.State.Invalidated

        shard.lifecycle.restart()
    }

    override fun Context.reconnect(event: ReconnectEvent): Kind<F, Unit> = CC.fx.concurrent {
        shard.state = Shard.State.Disconnected
        shard.lifecycle.restart()
    }
}