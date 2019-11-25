package org.tesserakt.diskordin.gateway.defaultHandlers

import arrow.typeclasses.Monad
import kotlinx.coroutines.ExperimentalCoroutinesApi
import mu.KLogging
import org.tesserakt.diskordin.core.data.event.lifecycle.HeartbeatACKEvent
import org.tesserakt.diskordin.core.data.event.lifecycle.HeartbeatEvent
import org.tesserakt.diskordin.core.data.event.lifecycle.HelloEvent
import org.tesserakt.diskordin.gateway.Gateway
import org.tesserakt.diskordin.gateway.json.commands.Heartbeat
import org.tesserakt.diskordin.gateway.sendPayload

@ExperimentalCoroutinesApi
internal class HeartbeatHandler<F>(override val gateway: Gateway<F>, M: Monad<F>) : GatewayHandler(), Monad<F> by M {
    private companion object : KLogging()

    private var interval = 0L

    init {
        fx.monad {
            val heartbeat = !gateway.eventDispatcher.subscribeOn<HeartbeatEvent>()
            val hello = !gateway.eventDispatcher.subscribeOn<HelloEvent>()
            val ack = !gateway.eventDispatcher.subscribeOn<HeartbeatACKEvent>()

            heartbeat.onEach {
                sendPayload(Heartbeat(it.v), gateway.lastSequenceId)
            }

            hello.onEach {
                interval = it.heartbeatInterval
            }
        }
    }
}