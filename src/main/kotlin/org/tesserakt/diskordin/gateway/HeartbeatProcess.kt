package org.tesserakt.diskordin.gateway

import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.milliseconds
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor.Context
import org.tesserakt.diskordin.gateway.json.commands.Heartbeat
import org.tesserakt.diskordin.gateway.shard.Shard
import java.time.Instant

class HeartbeatProcess(private val interval: Long) {
    private val power = interval / 1000

    fun <F> start(CC: Concurrent<F>, context: Context) = CC.fx.concurrent {
        var isExiting = false

        while (context.shard.state == Shard.State.Connected && !isExiting) {
            val result = !context.shard.connection.sendPayload(
                Heartbeat(context.shard.sequence),
                context.shard.sequence,
                context.shard.shardData.current,
                CC
            ).attempt()
            result.map { context.shard.lastHeartbeat = Instant.now() }

            var remaining = interval
            while (remaining > 0) {
                if (context.shard.state != Shard.State.Connected) {
                    isExiting = true
                    break
                }

                remaining -= power
                !sleep(power.milliseconds)
            }
        }
    }
}