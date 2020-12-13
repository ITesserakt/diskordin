package org.tesserakt.diskordin.impl.gateway.interceptor

import arrow.core.Either
import arrow.fx.coroutines.Schedule
import arrow.fx.coroutines.retry
import arrow.fx.coroutines.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.datetime.Clock
import org.tesserakt.diskordin.core.client.GatewayContext
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor.Context
import org.tesserakt.diskordin.gateway.interceptor.sendPayload
import org.tesserakt.diskordin.gateway.json.commands.Heartbeat
import org.tesserakt.diskordin.gateway.shard.Shard

class HeartbeatProcess(private val interval: Long) {
    suspend fun start(context: Context) {
        val coroutineContext = context.event.client.context[GatewayContext].scheduler
        val process = CoroutineScope(coroutineContext).async(start = CoroutineStart.LAZY) {
            while (true) {
                retry(Schedule.exponential(1.seconds)) {
                    Either.catch { context.sendPayload(Heartbeat(context.shard.sequence.value)) }
                        .map { context.shard._heartbeats.value = Clock.System.now() }
                }
                delay(interval)
            }
        }

        context.shard.state.collect {
            if (it == Shard.State.Connected && !process.isActive) process.start()
            else process.cancel()
        }
    }
}