package org.tesserakt.diskordin.gateway.defaultHandlers

import arrow.typeclasses.Monad
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.tesserakt.diskordin.core.data.event.lifecycle.HelloEvent
import org.tesserakt.diskordin.gateway.Gateway
import org.tesserakt.diskordin.gateway.json.commands.Heartbeat
import org.tesserakt.diskordin.gateway.json.commands.Identify
import org.tesserakt.diskordin.gateway.sendPayload

@ExperimentalCoroutinesApi
internal class HelloHandler<F>(override val gateway: Gateway<F>, M: Monad<F>) : GatewayHandler(), Monad<F> by M {
    private val dispatcher = gateway.eventDispatcher

    init {
        val token = gateway.getKoin().getProperty<String>("token")!!
        fx.monad {
            val helloEvents = !dispatcher.subscribeOn<HelloEvent>()
            val intervals = helloEvents.map { it.heartbeatInterval }
            intervals.forEach { _ ->
                sendPayload(
                    Identify(
                        token,
                        Identify.ConnectionProperties(
                            System.getProperty("os.name"),
                            "Diskordin",
                            "Diskordin"
                        ),
                        shard = arrayOf(0, 1)
                    ), gateway.lastSequenceId
                )
            }
            intervals.map { heartbeat(it) }
        }
    }

    @ExperimentalCoroutinesApi
    private fun heartbeat(interval: Long) = gateway.scope.launch {
        while (isActive) {
            delay(interval)
            sendPayload(Heartbeat(gateway.lastSequenceId), gateway.lastSequenceId)
        }
    }
}