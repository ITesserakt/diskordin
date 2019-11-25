package org.tesserakt.diskordin.gateway

import arrow.Kind
import arrow.free.fix
import arrow.fx.typeclasses.Async
import arrow.typeclasses.Traverse
import com.tinder.scarlet.websocket.WebSocketEvent.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.tesserakt.diskordin.core.client.EventDispatcher
import org.tesserakt.diskordin.gateway.defaultHandlers.GatewayHandler
import org.tesserakt.diskordin.gateway.defaultHandlers.HeartbeatHandler
import org.tesserakt.diskordin.gateway.defaultHandlers.HelloHandler
import org.tesserakt.diskordin.gateway.defaultHandlers.RestartHandler
import org.tesserakt.diskordin.impl.core.client.EventDispatcherImpl
import java.util.concurrent.CancellationException
import kotlin.time.ExperimentalTime

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE", "unused")
@ExperimentalCoroutinesApi
class Gateway<F> @ExperimentalTime constructor(
    A: Async<F>, T: Traverse<F>
) : KoinComponent, Async<F> by A {
    @ExperimentalCoroutinesApi
    internal val eventDispatcher: EventDispatcher<F> = EventDispatcherImpl(this, A, T)
    internal val scope: CoroutineScope = getKoin().getProperty("gatewayScope")!!
    internal var lastSequenceId: Int? = null

    private val logger = KotlinLogging.logger { }
    private val lifecycle by inject<GatewayLifecycle>()
    private val restartHandler = registerHandler { RestartHandler(this, this) }

    init {
        registerHandler { HelloHandler(this, this) }
        registerHandler { HeartbeatHandler(this, this) }
    }

    internal fun stop() {
        lifecycle.stop()
        scope.coroutineContext.cancel(CancellationException("Shutdown"))
    }

    internal fun restart() {
        lifecycle.restart()
        restartHandler.doRestart()
        lifecycle.start()
    }

    internal fun run(): NewGatewayAPI<Kind<F, Unit>> = GatewayAPIF.fx.monad {
        val fromDiscord = observeDiscordEvents<F>().bind().continueOn(scope.coroutineContext)
        val fromConnection = observeWebSocketEvents<F>().bind().continueOn(scope.coroutineContext)

        fromDiscord.flatTap {
            effect { lastSequenceId = it.seq ?: lastSequenceId }
        }.flatMap { payload ->
            eventDispatcher.publish(payload).fromEither { IllegalStateException(it.message) }
        }.flatMap { fromConnection }.flatTap {
            effect {
                when (it) {
                    is OnConnectionOpened -> logger.info("Gateway reached")
                    is OnConnectionFailed -> logger.error("WebSocket error", it)
                    is OnConnectionClosing -> logger.warn("WebSocket connection closing (${it.shutdownReason})")
                }
            }
        }.unit()
    }.fix()

    private fun <T : GatewayHandler> registerHandler(handler: (Gateway<F>) -> T): T = handler(this)
}