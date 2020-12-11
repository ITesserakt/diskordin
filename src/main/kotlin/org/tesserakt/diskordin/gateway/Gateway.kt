package org.tesserakt.diskordin.gateway

import arrow.core.Either
import arrow.core.extensions.either.applicative.applicative
import arrow.core.extensions.list.foldable.sequence_
import arrow.core.extensions.list.functor.fproduct
import arrow.core.fix
import arrow.core.getOrHandle
import arrow.fx.coroutines.Fiber
import arrow.fx.coroutines.ForkConnected
import arrow.fx.coroutines.parTraverse
import arrow.fx.coroutines.stream.Stream
import kotlinx.coroutines.ExperimentalCoroutinesApi
import mu.KotlinLogging
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.core.client.GatewayContext
import org.tesserakt.diskordin.core.client.ShardContext
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
    private val context: BootstrapContext,
    private val lifecycles: List<GatewayLifecycleManager>
) {
    private val tokenInterceptors = context[GatewayContext].interceptors
        .filter { it.selfContext == TokenInterceptor.Context::class } as List<Interceptor<Interceptor.Context>>

    @ExperimentalCoroutinesApi
    private val eventInterceptors = context[GatewayContext].interceptors
        .filter { it.selfContext == EventInterceptor.Context::class } as List<Interceptor<Interceptor.Context>>

    private val controller = ShardController(context[ShardContext], lifecycles)
    private val logger = KotlinLogging.logger("[Gateway]")

    @ExperimentalCoroutinesApi
    internal suspend fun run(): List<Stream<Fiber<Unit>>> =
        lifecycles.map { it.start(); it }.fproduct { it.connection.receive() }.withIndex().map { (index, it) ->
            val (lifecycle, events) = it
            val shard = Shard(
                context[ShardContext].token,
                Shard.Data(index, context[ShardContext].shardCount.extract()),
                lifecycle
            )

            events.map { WebSocketEventTransformer.transform(it) }
                .effectTap { shard._sequence.value = it.seq ?: shard.sequence.value }
                .effectMap { payload ->
                    ForkConnected {
                        val result = if (payload.isTokenPayload)
                            processIncoming(payload, tokenInterceptors, RawTokenTransformer) {
                                TokenInterceptor.Context(it, controller, shard)
                            }
                        else processIncoming(payload, eventInterceptors, RawEventTransformer) {
                            EventInterceptor.Context(it, controller, shard)
                        }

                        result.getOrHandle {
                            logger.error(it) { "Unexpected fail on interceptor while processing ${payload.name}#${payload.opcode}" }
                            logger.debug { payload.rawData }
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
    ) = Either.catchAndFlatten {
        @Suppress("UNCHECKED_CAST") val incoming = transformer.transform(payload as Payload<P>)
        @Suppress("NAME_SHADOWING") val interceptorContext = interceptorContext(incoming)

        interceptors.parTraverse { Either.catch { it.intercept(interceptorContext) } }
            .sequence_(Either.applicative()).fix()
    }

    abstract class Factory {
        private val gatewayVersion = 8
        private val encoding = "json"

        protected val gatewayUrl = { start: String, compression: String ->
            "$start/?v=$gatewayVersion&encoding=$encoding&compression=$compression"
        }

        abstract fun BootstrapContext.createGateway(): Gateway
    }
}