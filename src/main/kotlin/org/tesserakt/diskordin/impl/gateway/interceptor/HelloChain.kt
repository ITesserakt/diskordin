package org.tesserakt.diskordin.impl.gateway.interceptor

import kotlinx.coroutines.delay
import org.tesserakt.diskordin.core.data.event.lifecycle.HelloEvent
import org.tesserakt.diskordin.gateway.HeartbeatProcess
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor
import org.tesserakt.diskordin.gateway.shard.Shard

class HelloChain : EventInterceptor() {
    override suspend fun Context.hello(event: HelloEvent) {
        val interval = event.heartbeatInterval
        shard.state = Shard.State.Handshaking

        delay(5500 * shard.shardData.current.toLong())
        if (shard.isReady())
            controller.resumeShard(shard)
        else {
            controller.openShard(shard.shardData.current, shard::sequence)
        }

        while (!shard.isReady()) delay(100)
        HeartbeatProcess(interval).run(this)
    }
}