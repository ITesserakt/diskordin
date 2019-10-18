package ru.tesserakt.diskordin.gateway

import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.websocket.WebSocketEvent.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import ru.tesserakt.diskordin.core.client.EventDispatcher
import ru.tesserakt.diskordin.gateway.defaultHandlers.HelloHandler
import ru.tesserakt.diskordin.impl.core.client.EventDispatcherImpl
import ru.tesserakt.diskordin.util.Loggers
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

private const val gatewayVersion = 6
private const val encoding = "json"
internal var lastSequence: Int? = null
    private set

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
    private val scope: CoroutineScope = getKoin().getProperty("gatewayScope")!!
    private val logger by Loggers("[Gateway]")
    private val lifecycle by inject<GatewayLifecycle>()
    @ExperimentalCoroutinesApi
    internal val eventDispatcher: EventDispatcher = EventDispatcherImpl(api)

    internal fun stop() {
        lifecycle.stop()
        scope.cancel("Shutdown")
    }

    @ExperimentalCoroutinesApi
    internal fun restart() {
        lifecycle.restart()
        scope.coroutineContext[Job]?.cancelChildren(CancellationException("Restart"))
        run()
    }

    @ExperimentalCoroutinesApi
    internal fun run() = scope.launch {
        check(remaining > 0) { "Limit($limit) succeeded. Try after $resetAfter" }
        lifecycle.start()
        HelloHandler(this@Gateway)

        api.allPayloads()
            .onEach {
                //                if (it.opcode() == Opcode.DISPATCH) logger.debug("Got ${it.name}")
//                else logger.debug("Got ${it.opcode()}")
                lastSequence = it.seq ?: lastSequence
            }.onEach { eventDispatcher.publish(it) }
            .launchIn(this)

        api.observeWebSocketEvents().collect {
            when (it) {
                is OnConnectionOpened -> logger.info("Gateway reached")
                is OnConnectionFailed -> logger.error("WebSocket error", it)
                is OnConnectionClosed -> scope.cancel(it.shutdownReason.reason)
                is OnConnectionClosing -> logger.debug("WebSocket closing (${it.shutdownReason})")
            }
        }
    }
}