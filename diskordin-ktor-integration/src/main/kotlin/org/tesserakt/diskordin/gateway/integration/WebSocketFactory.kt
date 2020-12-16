package org.tesserakt.diskordin.gateway.integration

import arrow.core.Eval
import io.ktor.client.*
import io.ktor.client.request.*
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.core.client.ConnectionContext
import org.tesserakt.diskordin.core.client.GatewayContext
import org.tesserakt.diskordin.core.client.ShardContext
import org.tesserakt.diskordin.gateway.Gateway
import org.tesserakt.diskordin.gateway.KtorWebSocketLifecycleManager
import kotlin.coroutines.CoroutineContext

class WebSocketFactory(private val client: Eval<HttpClient>, private val gatewayContext: CoroutineContext) :
    Gateway.Factory() {
    override fun BootstrapContext.createGateway(): Gateway {
        val createLifecycle = { shardId: Int, client: Eval<HttpClient>, url: String ->
            KtorWebSocketLifecycleManager(shardId, client.extract(), gatewayContext) { url(url) }
        }

        val lifecycles: Eval<List<KtorWebSocketLifecycleManager>> = Eval.later {
            (0 until this[ShardContext].shardCount.extract()).map {
                createLifecycle(
                    it,
                    client,
                    gatewayUrl(this[ConnectionContext].url, this[ConnectionContext].compression)
                )
            }
        }

        return Gateway(this[GatewayContext].interceptors, gatewayContext, this[ShardContext], lifecycles.extract())
    }
}