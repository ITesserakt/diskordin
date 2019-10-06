package ru.tesserakt.diskordin.gateway

import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.websocket.WebSocketEvent.*
import io.reactivex.Flowable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactive.asPublisher
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import ru.tesserakt.diskordin.gateway.json.*
import ru.tesserakt.diskordin.gateway.json.commands.Identify
import ru.tesserakt.diskordin.gateway.json.events.Hello
import ru.tesserakt.diskordin.util.Loggers
import sun.awt.OSInfo
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

private const val gatewayVersion = 6
private const val encoding = "json"
internal var lastSequence: Int? = null
    private set

class Gateway @ExperimentalTime constructor(
    url: String,
    val limit: Int,
    private val remaining: Int,
    private val resetAfter: Duration
) : KoinComponent {
    private val compression = getKoin().getProperty("compression", "")
    private val fullUrl = "$url/?v=$gatewayVersion&encoding=$encoding&compression=$compression"
    private val scarlet by inject<Scarlet> { parametersOf(fullUrl) }
    private var api = scarlet.create<GatewayAPI>()
    private val gatewayContext: CoroutineContext = getKoin().getProperty("gatewayContext")!!
    private val scope = CoroutineScope(gatewayContext)
    private val token = getKoin().getProperty<String>("token")!!
    private val logger by Loggers("[Gateway]")

    internal fun stop() {
        scope.cancel("Shutdown")
    }

    private fun <T : IGatewayCommand> Payload<T>.sendWith(f: (Payload<T>) -> Boolean) {
        if (f(this)) logger.debug("Sent ${opcode()}")
        else logger.warn("${opcode()} was not send")
    }

    @ExperimentalCoroutinesApi
    internal fun run() = scope.launch {
        check(remaining > 0) { "Limit succeeded. Try after $resetAfter" }

        api.allPayloads()
            .onEach {
                if (it.opcode == 0)
                    logger.debug("Got ${it.name}")
                else
                    logger.debug("Got ${it.opcode()}")
            }.filter { it.opcode() == Opcode.DISPATCH }
            .onEach { }
            .launchIn(scope)

        api.observeHello().onEach {
            heartbeat(it)
            Identify(
                token,
                Identify.ConnectionProperties(OSInfo.getOSType().name.toLowerCase(), "Diskordin", "Diskordin"),
                shard = arrayOf(0, 1)
            ).wrapWith(Opcode.IDENTIFY, lastSequence).sendWith(api::identify)
        }.launchIn(scope)

        api.observeWebSocketEvents().collect {
            when (it) {
                is OnConnectionFailed -> logger.error("WebSocket error", it)
                is OnConnectionClosed -> logger.warn("WebSocket closed (${it.shutdownReason})")
                is OnConnectionClosing -> logger.debug("WebSocker closing (${it.shutdownReason})")
            }
        }
    }

    private fun heartbeat(hello: Hello) = scope.launch(Dispatchers.Unconfined) {
        api.observeHeartbeatACK().asPublisher().let { Flowable.fromPublisher(it) }
            .timeout(hello.heartbeatInterval + 20000, TimeUnit.MILLISECONDS)
            .doOnError {
                logger.error("Zombie process detected. Restarting...")
            }.onErrorResumeNext(api.observeHeartbeatACK().asPublisher().let { Flowable.fromPublisher(it) })
            .subscribe()

        while (scope.isActive) {
            Heartbeat(lastSequence).wrapWith(Opcode.HEARTBEAT, lastSequence).sendWith(api::heartbeat)
            delay(hello.heartbeatInterval)
        }
    }
}