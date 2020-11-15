package org.tesserakt.diskordin.gateway

import arrow.core.Either
import arrow.core.extensions.list.functor.fproduct
import arrow.core.getOrHandle
import arrow.fx.coroutines.Fiber
import arrow.fx.coroutines.ForkConnected
import arrow.fx.coroutines.parTraverse
import arrow.fx.coroutines.stream.Stream
import kotlinx.coroutines.ExperimentalCoroutinesApi
import mu.KotlinLogging
import org.tesserakt.diskordin.core.client.BootstrapContext
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

@Suppress("UNCHECKED_CAST")
class Gateway(
    private val context: BootstrapContext.Gateway,
    private val lifecycles: List<GatewayLifecycleManager>
) {
    private val tokenInterceptors = context.interceptors
        .filter { it.selfContext == TokenInterceptor.Context::class } as List<Interceptor<Interceptor.Context>>

    @ExperimentalCoroutinesApi
    private val eventInterceptors = context.interceptors
        .filter { it.selfContext == EventInterceptor.Context::class } as List<Interceptor<Interceptor.Context>>

    private val controller = ShardController(
        context.connectionContext.shardSettings,
        lifecycles
    )

    @ExperimentalCoroutinesApi
    internal suspend fun run(): List<Stream<Fiber<Unit>>> =
        lifecycles.map { it.start(); it }.fproduct { it.connection.receive() }.withIndex().map { (index, it) ->
            val (lifecycle, events) = it
            val shard = Shard(
                context.connectionContext.shardSettings.token,
                Shard.Data(index, context.connectionContext.shardSettings.shardCount.extract()),
                lifecycle
            )

            events.map { WebSocketEventTransformer.transform(it) }
                .effectTap { shard._sequence.value = it.seq ?: shard.sequence.value }
                .effectMap { payload ->
                    ForkConnected {
                        if (payload.isTokenPayload)
                            processIncoming(payload, tokenInterceptors, RawTokenTransformer) {
                                TokenInterceptor.Context(it, controller, shard)
                            }
                        else processIncoming(payload, eventInterceptors, RawEventTransformer) {
                            EventInterceptor.Context(it, controller, shard)
                        }
                    }
                }
        }

    @ExperimentalCoroutinesApi
    internal suspend fun close() = lifecycles.parTraverse {
        it.stop(1000, "Normal closing")
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
            val logger = KotlinLogging.logger("[${it.name}]")

            Either.catch { it.intercept(interceptorContext) }.getOrHandle { t ->
                logger.error(t) { "Unexpected fail on interceptor while processing ${payload.name}#${payload.opcode}" }
                logger.debug { payload.rawData }
            }
        }
    }

    abstract class Factory {
        private val gatewayVersion = 6
        private val encoding = "json"

        protected val gatewayUrl = { start: String, compression: String ->
            "$start/?v=$gatewayVersion&encoding=$encoding&compression=$compression"
        }

        abstract fun BootstrapContext.Gateway.createGateway(): Gateway
    }
}