package org.tesserakt.diskordin.impl.gateway.interceptor

import arrow.fx.coroutines.milliseconds
import arrow.fx.coroutines.sleep
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.tesserakt.diskordin.core.data.event.lifecycle.HelloEvent
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor
import org.tesserakt.diskordin.gateway.shard.Shard

class HelloChain : EventInterceptor() {
    @ExperimentalCoroutinesApi
    override suspend fun Context.hello(event: HelloEvent) {
        val interval = event.heartbeatInterval

        sleep((5500 * (shard.shardData.index)).milliseconds)
        if (shard.isReady())
            controller.resumeShard(shard)
        else {
            controller.openShard(shard.shardData.index, shard.sequence)
        }

        shard._state.value = Shard.State.Handshaking
        while (!shard.isReady()) {
            sleep(100.milliseconds)
        }
        HeartbeatProcess(interval).start(this)
    }
}