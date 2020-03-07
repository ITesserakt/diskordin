package org.tesserakt.diskordin.impl.gateway.interceptor

import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.milliseconds
import org.tesserakt.diskordin.core.data.event.lifecycle.HelloEvent
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor
import org.tesserakt.diskordin.gateway.shard.Shard

class HelloChain<F>(private val CC: Concurrent<F>) : EventInterceptor<F>(CC) {
    override fun Context.hello(event: HelloEvent) = CC.fx.concurrent {
        val interval = event.heartbeatInterval

        !sleep((5500 * (shard.shardData.current + 1)).milliseconds)
        !effect {
            if (shard.isReady() && shard.state != Shard.State.Invalidated)
                controller.resumeShard(shard)
            else {
                controller.openShard(shard.shardData.current, shard::sequence)
            }
        }

        !effect { shard.state = Shard.State.Handshaking }
        while (!shard.isReady()) !sleep(100.milliseconds)
        !HeartbeatProcess(interval).start(this, this@hello)
    }
}