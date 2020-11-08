package org.tesserakt.diskordin.gateway

import arrow.fx.coroutines.ConcurrentVar
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.runBlocking
import org.tesserakt.diskordin.core.client.GatewayLifecycleManager

class KtorWebSocketLifecycleManager(
    private val client: HttpClient,
    private val request: HttpRequestBuilder.() -> Unit
) : GatewayLifecycleManager {
    private val state = ConcurrentVar.unsafeEmpty<DefaultClientWebSocketSession>()

    override suspend fun start() {
        val session = client.webSocketSession(request)
        state.put(session)
    }

    override suspend fun stop(code: Short, message: String) {
        val session = state.take()
        session.close(CloseReason(code, message))
    }

    override suspend fun restart() {
        val session = state.read()
        session.close(CloseReason(CloseReason.Codes.SERVICE_RESTART, "Restarting"))
        state.take()
        state.put(client.webSocketSession(request))
    }

    override val connection: GatewayConnection by lazy { runBlocking { GatewayConnectionImpl(state.read()) } }
}