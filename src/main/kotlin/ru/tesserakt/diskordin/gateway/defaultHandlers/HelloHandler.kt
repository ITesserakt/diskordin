package ru.tesserakt.diskordin.gateway.defaultHandlers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.tesserakt.diskordin.core.data.event.lifecycle.HelloEvent
import ru.tesserakt.diskordin.gateway.Gateway
import ru.tesserakt.diskordin.gateway.json.commands.Heartbeat
import ru.tesserakt.diskordin.gateway.json.commands.Identify
import sun.awt.OSInfo

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
                            OSInfo.getOSType().name.toLowerCase(),
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