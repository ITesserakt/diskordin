package org.tesserakt.diskordin.gateway

import arrow.core.compose
import arrow.core.extensions.list.applicative.map
import arrow.core.extensions.list.traverse.sequence
import arrow.syntax.function.andThen
import arrow.syntax.function.memoize
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.LifecycleState
import com.tinder.scarlet.lifecycle.LifecycleRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import okhttp3.OkHttpClient
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.core.client.GatewayLifecycleManager
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor
import org.tesserakt.diskordin.gateway.interceptor.Interceptor
import org.tesserakt.diskordin.gateway.interceptor.TokenInterceptor
import org.tesserakt.diskordin.gateway.json.IPayload
import org.tesserakt.diskordin.gateway.json.Payload
import org.tesserakt.diskordin.gateway.shard.Shard
import org.tesserakt.diskordin.gateway.shard.ShardController
import org.tesserakt.diskordin.gateway.transformer.RawEventTransformer
import org.tesserakt.diskordin.gateway.transformer.RawTokenTransformer
import org.tesserakt.diskordin.gateway.transformer.Transformer
import org.tesserakt.diskordin.gateway.transformer.WebSocketEventTransformer
import org.tesserakt.diskordin.impl.core.client.setupScarlet

@Suppress("UNCHECKED_CAST")
class Gateway<F>(
    private val context: BootstrapContext.Gateway<F>,
    private val connections: List<GatewayConnection>,
    private val lifecycles: List<GatewayLifecycleManager>
) {
    private val tokenInterceptors = context.interceptors
        .filter { it.selfContext == TokenInterceptor.Context::class }
        .map { it as TokenInterceptor<F> }

    private val eventInterceptors = context.interceptors
        .filter { it.selfContext == EventInterceptor.Context::class }
        .map { it as EventInterceptor<F> }

    private val controller = ShardController(
        context.connectionContext.shardSettings,
        connections,
        lifecycles
    )

    @FlowPreview
    internal fun run(): Job {
        lifecycles.forEach { it.start() }

        return connections.map { it.receive().asFlow() }.withIndex().asFlow().flatMapMerge { (index, it) ->
            val shard = Shard(
                context.connectionContext.shardSettings.token,
                Shard.Data(index, context.connectionContext.shardSettings.shardCount),
                connections[index],
                lifecycles[index]
            )

            it.map { WebSocketEventTransformer.transform(it) }
                .onEach { shard.sequence = it.seq ?: shard.sequence }
                .map { payload ->
                    if (payload.isTokenPayload) processIncoming(payload, tokenInterceptors, RawTokenTransformer) {
                        TokenInterceptor.Context(it, controller, shard)
                    } else processIncoming(payload, eventInterceptors, RawEventTransformer) {
                        EventInterceptor.Context(it, controller, shard)
                    }
                }.map { context.runner.run { it.suspended() } }
        }.launchIn(CoroutineScope(context.scheduler))
    }

    internal fun close() {
        for (it in 0 until context.connectionContext.shardSettings.shardCount) {
            controller.closeShard(it)
        }
    }

    private fun <P : IPayload, C : Interceptor.Context, I : Interceptor<C, F>, E> processIncoming(
        payload: Payload<*>,
        interceptors: List<I>,
        transformer: Transformer<Payload<P>, E>,
        interceptorContext: (response: E) -> C
    ) = context.CC.run {
        interceptors.map {
            interceptorContext compose transformer andThen { ctx -> it.intercept(ctx).fork(context.scheduler) }
        }.map { it(payload as Payload<P>) }
            .sequence(this)
            .flatMap { unit() }
    }

    companion object Factory {
        private const val gatewayVersion = 6
        private const val encoding = "json"

        private val gatewayUrl = { start: String, compression: String ->
            "$start/?v=$gatewayVersion&encoding=$encoding&compression=$compression"
        }.memoize()

        private val lifecycle: (LifecycleRegistry) -> GatewayLifecycleManager = { registry: LifecycleRegistry ->
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
            scarlet(
                context.url,
                context.compression,
                lifecycle,
                context.httpClient.memoize().extract()
            ).create<GatewayConnection>()
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