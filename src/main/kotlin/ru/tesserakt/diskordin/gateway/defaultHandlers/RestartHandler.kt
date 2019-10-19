package ru.tesserakt.diskordin.gateway.defaultHandlers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.tesserakt.diskordin.core.data.event.lifecycle.ReadyEvent
import ru.tesserakt.diskordin.core.entity.IDiscordObject
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.Gateway
import ru.tesserakt.diskordin.gateway.json.commands.Resume

@ExperimentalCoroutinesApi
class RestartHandler(private val gateway: Gateway) : IDiscordObject {
    private val scope = getKoin().getProperty<CoroutineScope>("gatewayScope")!!
    private var sessionId: String? = null

    init {
        gateway.eventDispatcher.subscribeOn<ReadyEvent>()
            .onEach { sessionId = it.sessionId }
            .launchIn(scope)
    }

    fun doRestart() {
        checkNotNull(sessionId) { "Can`t restart if there was not any ready event!" }
        gateway.eventDispatcher.sendAnswer(
            Resume(
                client.token,
                gateway.lastSequenceId.toString(),
                sessionId!!
            )
        )
    }
}