package ru.tesserakt.diskordin.gateway.defaultHandlers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.tesserakt.diskordin.core.data.event.lifecycle.ReadyEvent
import ru.tesserakt.diskordin.gateway.Gateway
import ru.tesserakt.diskordin.gateway.json.commands.Resume

@ExperimentalCoroutinesApi
class RestartHandler(override val gateway: Gateway) : GatewayHandler() {
    private var sessionId: String? = null
    private val token = gateway.getKoin().getProperty<String>("token")!!

    init {
        gateway.eventDispatcher.subscribeOn<ReadyEvent>()
            .onEach { sessionId = it.sessionId }
            .launchIn(gateway.scope)
    }

    fun doRestart() {
        checkNotNull(sessionId) { "Can`t restart if there was not any ready event!" }
        gateway.eventDispatcher.sendAnswer(
            Resume(
                token,
                gateway.lastSequenceId.toString(),
                sessionId!!
            )
        )
    }
}