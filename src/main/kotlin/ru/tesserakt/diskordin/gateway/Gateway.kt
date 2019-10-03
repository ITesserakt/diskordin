package ru.tesserakt.diskordin.gateway

import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.wss
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readReason
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.get
import ru.tesserakt.diskordin.gateway.json.IRawEvent
import ru.tesserakt.diskordin.gateway.json.Opcode
import ru.tesserakt.diskordin.gateway.json.Payload
import ru.tesserakt.diskordin.gateway.json.events.HeartbeatACK
import ru.tesserakt.diskordin.util.Loggers
import ru.tesserakt.diskordin.util.fromJson
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
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
    val resetAfter: Duration
) : KoinComponent {
    private val compression = getKoin().getProperty("compression", "")
    private val fullUrl = "$url/?v=$gatewayVersion&encoding=$encoding}&compression=$compression"
    private val httpClient = get<HttpClient>()
    private val context = getKoin().getProperty<CoroutineContext>("gatewayContext")
        ?: throw IllegalStateException()
    private val logger = Loggers("[Gateway]")
    private val scope = CoroutineScope(context)
    private val processor: GatewayProcessor = GatewayProcessor(scope)

    @FlowPreview
    @ExperimentalTime
    internal suspend fun restart() {
        if (!scope.isActive) {
            logger.error("Gateway isn`t started.")
            return
        }
        logger.warn("Attempt to restart")
        scope.cancel("Restart")
        start()
    }

    internal fun stop() {
        if (!scope.isActive) {
            logger.error("Gateway isn`t started")
            return
        }
        scope.cancel("Shutdown")
    }

    @FlowPreview
    @ExperimentalTime
    internal suspend fun start() =
        scope.launch {
            check(remaining >= 0) { "There is no available sessions" }

            httpClient.wss(fullUrl) {
                for (frame in incoming) {
                    if (frame is Frame.Close) {
                        logger.warn(frame.readReason()?.message)
                        restart()
                    }
                    if (frame !is Frame.Text) continue
                    val payload = frame.readText().fromJson<Payload>()

                    lastSequence = payload.seq ?: lastSequence

                    val typeByOpcode = Opcode.values()
                        .filter { it.type.isSubclassOf(IRawEvent::class) }
                        .firstOrNull { it.ordinal == payload.opcode }?.type as KClass<out IRawEvent>?
                        ?: throw NoSuchElementException("Invalid payload opcode (${payload.opcode})")

                    val data =
                        if (typeByOpcode == HeartbeatACK::class) HeartbeatACK()
                        else payload.unwrap(typeByOpcode)

                    logger.debug("Got $data")
                    processor.answer(data, outgoing)
                }
            }
        }
}