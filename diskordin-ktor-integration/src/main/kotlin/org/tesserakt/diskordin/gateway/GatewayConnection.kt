package org.tesserakt.diskordin.gateway

import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import mu.KotlinLogging
import org.tesserakt.diskordin.gateway.json.Message
import org.tesserakt.diskordin.gateway.json.WebSocketEvent

class GatewayConnectionImpl(shardId: Int, private val session: DefaultClientWebSocketSession, job: Job) :
    GatewayConnection {
    private val logger = KotlinLogging.logger("[Shard #$shardId]")
    private val exceptionsHandler = CoroutineExceptionHandler { _, t ->
        logger.error(t) { "Cannot send or receive data to discord" }
    }
    private val scope = CoroutineScope(Dispatchers.Unconfined + job + exceptionsHandler)

    override suspend fun send(data: Message) = scope.launch {
        session.send(data.decompress())
    }

    override fun receive() = session.incoming.consumeAsFlow()
        .map(FrameTransformer::transform)
        .onStart { emit(WebSocketEvent.ConnectionOpened) }
        .catch { emit(WebSocketEvent.ConnectionFailed(it)) }
        .onCompletion { emit(WebSocketEvent.ConnectionClosed) }
        .shareIn(scope, SharingStarted.Lazily)
}