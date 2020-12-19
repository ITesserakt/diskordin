package org.tesserakt.diskordin.impl.gateway.interceptor

import arrow.fx.coroutines.Schedule
import arrow.fx.coroutines.retry
import arrow.fx.coroutines.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Clock
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor.Context
import org.tesserakt.diskordin.gateway.interceptor.sendPayload
import org.tesserakt.diskordin.gateway.json.commands.Heartbeat
import org.tesserakt.diskordin.gateway.shard.Shard

class HeartbeatProcess(private val interval: Long, private val scope: CoroutineScope) {
    suspend fun start(context: Context) {
        val process = scope.async(start = CoroutineStart.LAZY) {
            while (true) {
                retry(Schedule.exponential(1.seconds)) {
                    context.sendPayload(Heartbeat(context.shard.sequence.value))
                    context.shard._heartbeats.value = Clock.System.now()
                }
                delay(interval)
            }
        }

        context.shard.state.onEach {
            if (it == Shard.State.Connected && !process.isActive) process.start()
            else if (it != Shard.State.Connected && process.isActive) process.cancel()
        }.launchIn(scope)
    }
}