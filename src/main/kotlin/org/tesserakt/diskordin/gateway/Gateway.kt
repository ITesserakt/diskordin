package org.tesserakt.diskordin.gateway

import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.websocket.WebSocketEvent.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import org.tesserakt.diskordin.core.client.EventDispatcher
import org.tesserakt.diskordin.gateway.defaultHandlers.GatewayHandler
import org.tesserakt.diskordin.gateway.defaultHandlers.HeartbeatHandler
import org.tesserakt.diskordin.gateway.defaultHandlers.HelloHandler
import org.tesserakt.diskordin.gateway.defaultHandlers.RestartHandler
import org.tesserakt.diskordin.impl.core.client.EventDispatcherImpl
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

private const val gatewayVersion = 6
private const val encoding = "json"

@ExperimentalCoroutinesApi
class Gateway @ExperimentalTime constructor(
    url: String,
    private val limit: Int,
    private val remaining: Int,
    private val resetAfter: Duration
) : KoinComponent {
    private val compression = getKoin().getProperty("compression", "")
    private val fullUrl = "$url/?v=$gatewayVersion&encoding=$encoding&compression=$compression"
    private val scarlet by inject<Scarlet> { parametersOf(fullUrl) }
    private val api = scarlet.create<GatewayAPI>()

    @ExperimentalCoroutinesApi
    internal val eventDispatcher: EventDispatcher = EventDispatcherImpl(this, api)
    internal val scope: CoroutineScope = getKoin().getProperty("gatewayScope")!!
    internal var lastSequenceId: Int? = null

    private val logger = KotlinLogging.logger { }
    private val lifecycle by inject<GatewayLifecycle>()
    private val restartHandler = registerHandler(::RestartHandler)

    init {
        registerHandler(::HelloHandler)
        registerHandler(::HeartbeatHandler)
    }

    internal fun stop() {
        lifecycle.stop()
        scope.cancel("Shutdown")
    }

    internal fun restart() {
        lifecycle.restart()
        restartHandler.doRestart()
        lifecycle.start()
    }

    internal fun run() = scope.launch {
        check(remaining > 0) { "Limit($limit) succeeded. Try after $resetAfter" }
        lifecycle.start()

        api.observeDiscordEvents()
            .onEach(eventDispatcher::publish)
            .onEach { lastSequenceId = it.seq ?: lastSequenceId }
            .launchIn(this)

        api.observeWebSocketEvents().collect {
            when (it) {
                is OnConnectionOpened -> logger.info("Gateway reached")
                is OnConnectionFailed -> {
                    logger.error("WebSocket error", it)
                    return@collect
                }
                is OnConnectionClosing -> {
                    logger.debug("WebSocket closing (${it.shutdownReason})")
                    return@collect
                }
            }
        }
    }

    private fun <T : GatewayHandler> registerHandler(handler: (Gateway) -> T): T = handler(this)
}