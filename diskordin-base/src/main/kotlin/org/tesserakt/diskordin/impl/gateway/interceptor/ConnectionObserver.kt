package org.tesserakt.diskordin.impl.gateway.interceptor

import arrow.fx.coroutines.Schedule.Companion.spaced
import mu.KotlinLogging
import org.tesserakt.diskordin.gateway.shard.Shard
import org.tesserakt.diskordin.gateway.shard.ShardController
import kotlin.time.ExperimentalTime
import kotlin.time.minutes
import kotlin.time.seconds

class ConnectionObserver(private val shardController: ShardController) {
    private val logger = KotlinLogging.logger { }

    @OptIn(ExperimentalTime::class)
    suspend fun observe(shard: Shard) = spaced<Unit>(1.seconds).repeat {
        if (shard._state.value == Shard.State.Connected && shard.ping() >= 1.minutes) {
            logger.warn("Shard #${shard.shardData.index} does not respond. Restarting")
            shardController.closeShard(shard.shardData.index)
            shardController.resumeShard(shard)
        }
    }
}