package org.tesserakt.diskordin.impl.gateway.interceptor

import arrow.fx.coroutines.Schedule
import arrow.fx.coroutines.repeat
import arrow.fx.coroutines.seconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import mu.KotlinLogging
import org.tesserakt.diskordin.gateway.shard.Shard
import org.tesserakt.diskordin.gateway.shard.ShardController
import kotlin.time.ExperimentalTime
import kotlin.time.minutes

class ConnectionObserver(private val shardController: ShardController) {
    private val logger = KotlinLogging.logger { }

    @OptIn(ExperimentalTime::class)
    @ExperimentalCoroutinesApi
    suspend fun observe(shard: Shard) = repeat(Schedule.spaced(1.seconds)) {
        if (shard._state.value == Shard.State.Connected && shard.ping() >= 1.minutes) {
            logger.warn("Shard #${shard.shardData.index} does not respond. Restarting")
            shardController.closeShard(shard.shardData.index)
            shardController.resumeShard(shard)
        }
    }
}