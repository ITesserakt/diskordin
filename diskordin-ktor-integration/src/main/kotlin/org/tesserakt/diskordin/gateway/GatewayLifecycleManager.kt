package org.tesserakt.diskordin.gateway

import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import mu.KotlinLogging
import kotlin.coroutines.CoroutineContext

class KtorWebSocketLifecycleManager(
    private val shardId: Int,
    private val client: HttpClient,
    private val gatewayContext: CoroutineContext,
    private val request: HttpRequestBuilder.() -> Unit
) : GatewayLifecycleManager {
    private val logger = KotlinLogging.logger { }
    private val job = SupervisorJob(client.coroutineContext[Job])
    override lateinit var coroutineScope: CoroutineScope private set
    private lateinit var session: DefaultClientWebSocketSession
    private val exceptionHandler = CoroutineExceptionHandler { _, t ->
        logger.error(t) { "Cannot send or receive data to discord" }
    }

    override suspend fun start() {
        coroutineScope = CoroutineScope(gatewayContext + job + exceptionHandler)
        session = client.webSocketSession(request)
    }

    override suspend fun stop(code: Short, message: String) {
        coroutineScope.cancel()
        session.close(CloseReason(code, message))
    }

    override suspend fun restart() {
        job.cancelChildren()
        session.close(CloseReason(CloseReason.Codes.SERVICE_RESTART, "Restarting"))
        session = client.webSocketSession(request)
    }

    override val connection: GatewayConnection by lazy { GatewayConnectionImpl(shardId, session, Job(job)) }
}