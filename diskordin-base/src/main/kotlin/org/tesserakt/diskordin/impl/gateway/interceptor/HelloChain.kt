package org.tesserakt.diskordin.impl.gateway.interceptor

import kotlinx.coroutines.delay
import org.tesserakt.diskordin.core.data.event.lifecycle.HelloEvent
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor
import org.tesserakt.diskordin.gateway.shard.Shard

class HelloChain : EventInterceptor() {
    override suspend fun Context.hello(event: HelloEvent) {
        val interval = event.heartbeatInterval

        delay(5500L * shard.shardData.index)
        if (shard.isReady())
            controller.resumeShard(shard)
        else {
            controller.openShard(shard.shardData.index, shard.sequence)
        }

        shard._state.value = Shard.State.Handshaking
        HeartbeatProcess(interval, scope).start(this)
    }
}