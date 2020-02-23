package org.tesserakt.diskordin.gateway

import kotlinx.coroutines.delay
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor.Context
import org.tesserakt.diskordin.gateway.json.commands.Heartbeat
import org.tesserakt.diskordin.gateway.shard.Shard
import java.time.Instant

class HeartbeatProcess(private val interval: Long) {
    private val power = interval / 1000

    suspend fun run(context: Context) = context.run {
        while (context.shard.state == Shard.State.Connected) {
            val result = context.shard.connection
                .sendPayload(Heartbeat(context.shard.sequence), context.shard.sequence, context.shard.shardData.current)
            if (result) shard.lastHeartbeat = Instant.now()

            var remaining = interval
            while (remaining > 0) {
                if (context.shard.state != Shard.State.Connected)
                    return

                remaining -= power
                delay(power)
            }
        }
    }
}