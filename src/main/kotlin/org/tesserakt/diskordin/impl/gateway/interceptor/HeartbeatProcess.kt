package org.tesserakt.diskordin.impl.gateway.interceptor

import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.milliseconds
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor.Context
import org.tesserakt.diskordin.gateway.interceptor.sendPayload
import org.tesserakt.diskordin.gateway.json.commands.Heartbeat
import org.tesserakt.diskordin.gateway.shard.Shard
import java.time.Instant

class HeartbeatProcess(private val interval: Long) {
    private val power = 100

    fun <F> start(CC: Concurrent<F>, context: Context) = CC.fx.concurrent {
        var isExiting = false

        while (!isExiting) {
            val result = !context.sendPayload(Heartbeat(context.shard.sequence), CC).attempt()
            result.map { context.shard.lastHeartbeat = Instant.now() }

            isExiting = !waitForInterval(interval) { context.shard.state }
        }
    }

    private inline fun <F> Concurrent<F>.waitForInterval(interval: Long, crossinline state: () -> Shard.State) =
        fx.concurrent {
            var isExiting = false
            var remaining = interval
            while (remaining > 0) {
                if (state() != Shard.State.Connected) {
                    isExiting = true
                    break
                }

                remaining -= power
                !sleep(power.milliseconds)
            }
            isExiting
        }
}