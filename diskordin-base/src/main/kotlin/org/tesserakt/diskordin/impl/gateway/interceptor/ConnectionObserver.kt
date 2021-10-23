package org.tesserakt.diskordin.impl.gateway.interceptor

import arrow.fx.coroutines.Schedule.Companion.spaced
import mu.KotlinLogging
import org.tesserakt.diskordin.gateway.shard.Shard
import org.tesserakt.diskordin.gateway.shard.ShardController
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class ConnectionObserver(private val shardController: ShardController) {
    private val logger = KotlinLogging.logger { }

    @OptIn(ExperimentalTime::class)
    suspend fun observe(shard: Shard) = spaced<Unit>(1e9).repeat {
        if (shard._state.value == Shard.State.Connected && shard.ping() >= Duration.minutes(1)) {
            logger.warn("Shard #${shard.shardData.index} does not respond. Restarting")
            shardController.closeShard(shard.shardData.index)
            shardController.resumeShard(shard)
        }
    }
}