package ru.tesserakt.diskordin.gateway.defaultHandlers

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.core.KoinComponent
import ru.tesserakt.diskordin.core.data.event.lifecycle.HelloEvent
import ru.tesserakt.diskordin.gateway.Gateway
import ru.tesserakt.diskordin.gateway.json.Heartbeat
import ru.tesserakt.diskordin.gateway.json.commands.Identify
import ru.tesserakt.diskordin.util.Loggers
import sun.awt.OSInfo

@ExperimentalCoroutinesApi
internal class HelloHandler(private val gateway: Gateway) : KoinComponent {
    private val logger by Loggers("[Gateway]")
    private val dispatcher = gateway.eventDispatcher
    private val scope = getKoin().getProperty<CoroutineScope>("gatewayScope")!!

    init {
        val token = getKoin().getProperty<String>("token")!!
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
            .launchIn(scope)
    }

    @ExperimentalCoroutinesApi
    private fun heartbeat(interval: Long) = scope.launch {
        while (isActive) {
            delay(interval)
            dispatcher.sendAnswer(Heartbeat(gateway.lastSequenceId))
        }
    }
}