package org.tesserakt.diskordin.gateway.integration

import arrow.core.Eval
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.LifecycleRegistry
import com.tinder.scarlet.retry.BackoffStrategy
import okhttp3.OkHttpClient
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.core.client.ConnectionContext
import org.tesserakt.diskordin.core.client.ShardContext
import org.tesserakt.diskordin.gateway.Gateway
import org.tesserakt.diskordin.gateway.ScarletGatewayLifecycleManager

class ScarletFactory(
    private val httpClient: Eval<OkHttpClient>,
    private val strategy: BackoffStrategy,
    private val isDebug: Boolean
) : Gateway.Factory() {
    private val scarlets = mutableMapOf<Int, Eval<Scarlet>>()

    override fun BootstrapContext.createGateway(): Gateway {
        val createLifecycle = { registry: LifecycleRegistry, id: Int ->
            ScarletGatewayLifecycleManager(registry) { scarlets[id]?.extract() ?: error("IMPOSSIBLE") }
        }

        val createScarlet: (String, String, Lifecycle, BackoffStrategy, Boolean, Int) -> Unit =
            { start: String, compression: String, lifecycle: Lifecycle, strategy: BackoffStrategy, debug: Boolean, id: Int ->
                val scarlet by ScarletService(httpClient, gatewayUrl(start, compression), lifecycle, strategy, debug)
                scarlets[id] = scarlet
            }

        val lifecycles = (0 until this[ShardContext].shardCount.extract()).map {
            val lifecycle = createLifecycle(LifecycleRegistry(), it)
            createScarlet(
                this[ConnectionContext].url,
                this[ConnectionContext].compression,
                lifecycle,
                strategy,
                isDebug,
                it
            )

            lifecycle
        }

        return Gateway(this, lifecycles)
    }
}