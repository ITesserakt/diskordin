package org.tesserakt.diskordin.gateway

import arrow.syntax.function.memoize
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.LifecycleState
import com.tinder.scarlet.lifecycle.LifecycleRegistry
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import okhttp3.OkHttpClient
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.core.client.GatewayLifecycleManager
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor
import org.tesserakt.diskordin.gateway.interceptor.Interceptor
import org.tesserakt.diskordin.gateway.interceptor.TokenInterceptor
import org.tesserakt.diskordin.gateway.json.IPayload
import org.tesserakt.diskordin.gateway.json.IRawEvent
import org.tesserakt.diskordin.gateway.json.IToken
import org.tesserakt.diskordin.gateway.json.Payload
import org.tesserakt.diskordin.gateway.shard.Shard
import org.tesserakt.diskordin.gateway.shard.ShardController
import org.tesserakt.diskordin.gateway.transformer.RawEventTransformer
import org.tesserakt.diskordin.gateway.transformer.RawTokenTransformer
import org.tesserakt.diskordin.gateway.transformer.WebSocketEventTransformer
import org.tesserakt.diskordin.impl.core.client.setupScarlet

@Suppress("UNCHECKED_CAST")
class Gateway<F>(
    private val context: BootstrapContext.Gateway<F>,
    private val connections: List<GatewayConnection>,
    private val lifecycles: List<GatewayLifecycleManager>
) {
    private val controller = ShardController(
        context.connectionContext.shardSettings,
        connections,
        lifecycles
    )

    @FlowPreview
    @ExperimentalCoroutinesApi
    internal fun run(): Job {
        lifecycles.forEach { it.start() }

        val transformed = connections.mapIndexed { index, c -> index to c.receive().asFlow() }.asFlow()
        val chain = transformed.flatMapMerge { (index, connection) ->
            val shard = Shard(
                context.connectionContext.shardSettings.token,
                Shard.Data(index, context.connectionContext.shardSettings.shardCount),
                connections[index],
                lifecycles[index]
            )

            connection.map { WebSocketEventTransformer.transform(it) }
                .flowOn(context.scheduler)
                .onEach { shard.sequence = it.seq ?: shard.sequence }
                .flatMapMerge {
                    if (it.isTokenPayload) composeToken(it, shard)
                    else composeEvent(it, shard)
                }.map(context.runner)
        }

        return CoroutineScope(context.scheduler).launch {
            chain.retry { it !is CancellationException }.collect()
        }
    }

    internal fun close() {
        for (it in 0 until context.connectionContext.shardSettings.shardCount) {
            controller.closeShard(it)
        }
    }

    private fun composeToken(
        payload: Payload<out IPayload>,
        shard: Shard
    ) = context.CC.run {
        val token = RawTokenTransformer.transform(payload as Payload<IToken>)
        val interceptorContext = TokenInterceptor.Context(token, controller, shard)

        context.interceptors
            .filter { it.selfContext == TokenInterceptor.Context::class }
            .map { it as Interceptor<Interceptor.Context, F> }
            .map { it.intercept(interceptorContext) }
            .asFlow()
    }

    private fun composeEvent(
        payload: Payload<out IPayload>,
        shard: Shard
    ) = context.CC.run {
        val event = RawEventTransformer.transform(payload as Payload<IRawEvent>)
        val interceptorContext = EventInterceptor.Context(event, controller, shard)

        context.interceptors
            .filter { it.selfContext == EventInterceptor.Context::class }
            .map { it as Interceptor<Interceptor.Context, F> }
            .map { it.intercept(interceptorContext) }
            .asFlow()
    }

    companion object Factory {
        private const val gatewayVersion = 6
        private const val encoding = "json"

        private val gatewayUrl = { start: String, compression: String ->
            "$start/?v=$gatewayVersion&encoding=$encoding&compression=$compression"
        }.memoize()

        private val lifecycle = { registry: LifecycleRegistry ->
            object : GatewayLifecycleManager, Lifecycle by registry {
                override fun start() {
                    registry.onNext(LifecycleState.Started)
                }

                override fun stop() {
                    registry.onNext(LifecycleState.Stopped)
                }

                override fun restart() {
                    registry.onNext(LifecycleState.Stopped)
                    registry.onNext(LifecycleState.Started)
                }
            }
        }

        private val scarlet = { start: String, compression: String, lifecycle: Lifecycle, httpClient: OkHttpClient ->
            setupScarlet(gatewayUrl(start, compression), lifecycle, httpClient)
        }

        private val connection = { context: BootstrapContext.Gateway.Connection, lifecycle: Lifecycle ->
            scarlet(context.url, context.compression, lifecycle, context.httpClient).create<GatewayConnection>()
        }

        private val connections = { context: BootstrapContext.Gateway.Connection ->
            (0 until context.shardSettings.shardCount).map {
                val lifecycle: GatewayLifecycleManager = lifecycle(LifecycleRegistry())
                connection(context, lifecycle) to lifecycle
            }
        }

        fun <F> create(context: BootstrapContext.Gateway<F>): Gateway<F> {
            val connections = connections(context.connectionContext)
            return Gateway(context, connections.map { it.first }, connections.map { it.second })
        }
    }
}