package org.tesserakt.diskordin.gateway.integration

import arrow.core.Eval
import io.ktor.client.*
import io.ktor.client.request.*
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.gateway.Gateway
import org.tesserakt.diskordin.gateway.KtorWebSocketLifecycleManager

class WebSocketFactory(private val client: Eval<HttpClient>) : Gateway.Factory() {
    override fun BootstrapContext.Gateway.createGateway(): Gateway {
        val createLifecycle = { client: HttpClient, url: String ->
            KtorWebSocketLifecycleManager(client) { url(url) }
        }

        val lifecycles = (0 until connectionContext.shardSettings.shardCount.extract()).map {
            createLifecycle(client.extract(), connectionContext.url)
        }

        return Gateway(this, lifecycles)
    }
}