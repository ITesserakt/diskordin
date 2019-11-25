package org.tesserakt.diskordin.gateway.defaultHandlers

import arrow.typeclasses.Monad
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.tesserakt.diskordin.core.data.event.lifecycle.ReadyEvent
import org.tesserakt.diskordin.gateway.Gateway
import org.tesserakt.diskordin.gateway.json.commands.Resume
import org.tesserakt.diskordin.gateway.sendPayload

@ExperimentalCoroutinesApi
class RestartHandler<F>(override val gateway: Gateway<F>, M: Monad<F>) : GatewayHandler(), Monad<F> by M {
    private var sessionId: String? = null
    private val token = gateway.getKoin().getProperty<String>("token")!!

    init {
        fx.monad {
            gateway.eventDispatcher.subscribeOn<ReadyEvent>().bind()
                .onEach { sessionId = it.sessionId }
        }
    }

    fun doRestart() {
        checkNotNull(sessionId) { "Can`t restart if there was not any ready event!" }
        sendPayload(
            Resume(
                token,
                gateway.lastSequenceId.toString(),
                sessionId!!
            ), gateway.lastSequenceId
        )
    }
}