package org.tesserakt.diskordin.impl.gateway.interceptor

import arrow.core.Either
import arrow.fx.coroutines.ForkConnected
import arrow.fx.coroutines.Schedule
import arrow.fx.coroutines.retry
import arrow.fx.coroutines.seconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor.Context
import org.tesserakt.diskordin.gateway.interceptor.sendPayload
import org.tesserakt.diskordin.gateway.json.commands.Heartbeat
import org.tesserakt.diskordin.gateway.shard.Shard
import java.time.Instant

class HeartbeatProcess(private val interval: Long) {
    private data class RestartException(val shardId: Int) : Throwable("Shard #$shardId is going to restart")

    @ExperimentalCoroutinesApi
    suspend fun start(context: Context) {
        context.shard.state.map {
            if (it != Shard.State.Connected)
                throw RestartException(context.shard.shardData.index)

            ForkConnected {
                while (true) {
                    retry(Schedule.exponential(1.seconds)) {
                        Either.catch { context.sendPayload(Heartbeat(context.shard.sequence.value)) }
                            .map { context.shard._heartbeats.value = Instant.now() }
                    }
                    delay(interval)
                }
            }
        }.catch {
            if (it is RestartException) {
                context.controller.closeShard(it.shardId)
                context.controller.openShard(it.shardId, context.shard.sequence)
            }
        }.collect()
    }
}