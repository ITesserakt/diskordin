package ru.tesserakt.diskordin.gateway.defaultHandlers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.rx2.asFlowable
import ru.tesserakt.diskordin.core.data.event.lifecycle.HeartbeatACKEvent
import ru.tesserakt.diskordin.core.data.event.lifecycle.HeartbeatEvent
import ru.tesserakt.diskordin.core.data.event.lifecycle.HelloEvent
import ru.tesserakt.diskordin.gateway.Gateway
import ru.tesserakt.diskordin.gateway.json.Heartbeat
import ru.tesserakt.diskordin.util.Loggers
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime

@UseExperimental(ExperimentalTime::class)
@ExperimentalCoroutinesApi
internal class HeartbeatHandler(override val gateway: Gateway) : GatewayHandler() {
    private val logger by Loggers
    private var interval = 0L

    init {
        gateway.eventDispatcher.subscribeOn<HeartbeatEvent>()
            .onEach { gateway.eventDispatcher.sendAnswer(Heartbeat(it.v)) }
            .launchIn(gateway.scope)

        gateway.eventDispatcher.subscribeOn<HelloEvent>()
            .onEach { interval = it.heartbeatInterval }
            .launchIn(gateway.scope)

        gateway.eventDispatcher.subscribeOn<HeartbeatACKEvent>().asFlowable()
            .timeInterval().asFlow()
            .map {
                it.time(TimeUnit.MILLISECONDS)
            }.onEach {
                val diff = it - interval
                val sign = when {
                    diff > 0 -> "+"
                    else -> ""
                }
                logger.debug("Received heartbeat ACK after ${it}ms ($sign${diff}ms)")

                if (diff > 20000) {
                    logger.error("Gateway does not responding. Attempt to restart.")
                    gateway.restart()
                }
            }.launchIn(gateway.scope)
    }
}