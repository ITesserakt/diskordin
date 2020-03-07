package org.tesserakt.diskordin.impl.gateway.interceptor

import arrow.fx.IO
import arrow.fx.Schedule
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.monad.flatTap
import arrow.fx.extensions.io.monad.monad
import arrow.fx.repeat
import arrow.fx.typeclasses.seconds
import kotlinx.coroutines.Dispatchers
import mu.KotlinLogging
import org.tesserakt.diskordin.gateway.shard.Shard

class ConnectionObserver {
    private val logger = KotlinLogging.logger { }

    fun observe(shard: Shard) = IO.effect(Dispatchers.Unconfined) { shard.state }
        .flatTap {
            if (it == Shard.State.Connected && shard.ping() >= 60000) {
                logger.warn("Shard #${shard.shardData.current} does not respond. Restarting")
                shard.state = Shard.State.Disconnected
                shard.lifecycle.restart()
            }
            IO.unit
        }.repeat(IO.concurrent(), Schedule.spaced(IO.monad(), 1.seconds))
}