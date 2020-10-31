package org.tesserakt.diskordin.gateway

import arrow.core.Either
import arrow.core.identity
import arrow.fx.coroutines.Fiber
import arrow.fx.coroutines.ForkConnected
import arrow.fx.coroutines.parTraverse
import arrow.fx.coroutines.stream.Stream
import arrow.fx.coroutines.stream.flatten
import arrow.syntax.function.memoize
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.LifecycleState
import com.tinder.scarlet.lifecycle.LifecycleRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import mu.KotlinLogging
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
class Gateway(
    private val context: BootstrapContext.Gateway,
    private val connections: List<GatewayConnection>,
    private val lifecycles: List<GatewayLifecycleManager>
) {
    private val tokenInterceptors = context.interceptors
        .filter { it.selfContext == TokenInterceptor.Context::class } as List<Interceptor<Interceptor.Context>>

    private val eventInterceptors = context.interceptors
        .filter { it.selfContext == EventInterceptor.Context::class } as List<Interceptor<Interceptor.Context>>

    private val controller = ShardController(
        context.connectionContext.shardSettings,
        connections,
        lifecycles
    )

    @ExperimentalCoroutinesApi
    internal fun run(): Stream<Fiber<Unit>> {
        lifecycles.forEach { it.start() }

        return Stream.iterable(connections.map { it.receive() }.withIndex())
            .effectMap { (index, it) ->
                val shard = Shard(
                    context.connectionContext.shardSettings.token,
                    Shard.Data(index, context.connectionContext.shardSettings.shardCount.extract()),
                    connections[index],
                    lifecycles[index]
                )

                it.map { WebSocketEventTransformer.transform(it) }
                    .effectTap { shard._sequence.value = it.seq ?: shard.sequence.value }
                    .effectMap { payload ->
                        ForkConnected {
                            if (payload.isTokenPayload)
                                processIncoming(payload, tokenInterceptors, RawTokenTransformer) {
                                    TokenInterceptor.Context(it, controller, shard)
                                } else processIncoming(payload, eventInterceptors, RawEventTransformer) {
                                EventInterceptor.Context(it, controller, shard)
                            }
                        }
                    }
            }.flatten()
    }

    @ExperimentalCoroutinesApi
    internal fun close() {
        for (it in 0 until context.connectionContext.shardSettings.shardCount.extract()) {
            controller.closeShard(it)
        }
    }

    private suspend fun <P : IPayload, C : Interceptor.Context, I : Interceptor<C>, E> processIncoming(
        payload: Payload<*>,
        interceptors: List<I>,
        transformer: Transformer<Payload<P>, E>,
        interceptorContext: (response: E) -> C
    ) {
        @Suppress("UNCHECKED_CAST") val incoming = transformer.transform(payload as Payload<P>)
        @Suppress("NAME_SHADOWING") val interceptorContext = interceptorContext(incoming)

        interceptors.parTraverse {
            Either.catch { it.intercept(interceptorContext) } to KotlinLogging.logger("[${it.name}]")
        }.forEach { (either, logger) ->
            either.fold({
                logger.error(it) { "Unexpected fail on interceptor while processing ${payload.name}#${payload.opcode}" }
                logger.debug { payload.rawData }
            }, ::identity)
        }
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

        private val scarlet = { start: String, compression: String, lifecycle: Lifecycle ->
            setupScarlet(gatewayUrl(start, compression), lifecycle)
        }

        private val connection = { context: BootstrapContext.Gateway.Connection, lifecycle: Lifecycle ->
            scarlet(context.url, context.compression, lifecycle).create<GatewayConnection>()
        }

        private val connections = { context: BootstrapContext.Gateway.Connection ->
            (0 until context.shardSettings.shardCount.extract()).map {
                val lifecycle: GatewayLifecycleManager = lifecycle(LifecycleRegistry())
                connection(context, lifecycle) to lifecycle
            }
        }

        fun create(context: BootstrapContext.Gateway): Gateway {
            val connections = connections(context.connectionContext)
            return Gateway(context, connections.map { it.first }, connections.map { it.second })
        }
    }
}