package org.tesserakt.diskordin.gateway.defaultHandlers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.tesserakt.diskordin.core.data.event.lifecycle.HelloEvent
import org.tesserakt.diskordin.gateway.Gateway
import org.tesserakt.diskordin.gateway.json.commands.Heartbeat
import org.tesserakt.diskordin.gateway.json.commands.Identify

@ExperimentalCoroutinesApi
internal class HelloHandler(override val gateway: Gateway) : GatewayHandler() {
    private val dispatcher = gateway.eventDispatcher

    init {
        val token = gateway.getKoin().getProperty<String>("token")!!
        dispatcher.subscribeOn<HelloEvent>()
            .map { it.heartbeatInterval }
            .onEach {
                dispatcher.sendAnswer(
                    Identify(
                        token,
                        Identify.ConnectionProperties(
                            System.getProperty("os.name"),
                            "Diskordin",
                            "Diskordin"
                        ),
                        shard = arrayOf(0, 1)
                    )
                )
            }.map { heartbeat(it) }
            .launchIn(gateway.scope)
    }

    @ExperimentalCoroutinesApi
    private fun heartbeat(interval: Long) = gateway.scope.launch {
        while (isActive) {
            delay(interval)
            dispatcher.sendAnswer(Heartbeat(gateway.lastSequenceId))
        }
    }
}