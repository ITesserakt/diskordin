package ru.tesserakt.diskordin.gateway.defaultHandlers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.tesserakt.diskordin.core.data.event.lifecycle.HeartbeatEvent
import ru.tesserakt.diskordin.gateway.Gateway
import ru.tesserakt.diskordin.gateway.json.Heartbeat

@ExperimentalCoroutinesApi
class HeartbeatHandler(gateway: Gateway) {
    init {
        gateway.eventDispatcher.subscribeOn<HeartbeatEvent>()
            .onEach { gateway.eventDispatcher.sendAnswer(Heartbeat(it.v)) }
            .launchIn(gateway.scope)
    }
}