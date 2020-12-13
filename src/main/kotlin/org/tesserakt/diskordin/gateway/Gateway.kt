package org.tesserakt.diskordin.gateway

import arrow.core.Either
import arrow.core.extensions.either.applicativeError.handleErrorWith
import arrow.core.extensions.list.functor.fproduct
import arrow.core.getOrHandle
import arrow.core.left
import arrow.fx.coroutines.parTraverse
import kotlinx.coroutines.*
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

    private val eventInterceptors = context[GatewayContext].interceptors
        .filter { it.selfContext == EventInterceptor.Context::class } as List<Interceptor<Interceptor.Context>>

    private val loggers = context[GatewayContext].interceptors.associateBy(
        { it.name }, { KotlinLogging.logger(it.name) }
    )

    private val controller = ShardController(context[ShardContext], lifecycles)
    private val mainLogger = KotlinLogging.logger("[Gateway]")

    internal suspend fun run() =
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
                    if (payload.isTokenPayload)
                        processIncomingAsync(payload, tokenInterceptors, RawTokenTransformer) {
                            TokenInterceptor.Context(it, controller, shard)
                        }
                    else processIncomingAsync(payload, eventInterceptors, RawEventTransformer) {
                        EventInterceptor.Context(it, controller, shard)
                    }
                }
        }

    @ExperimentalCoroutinesApi
    internal suspend fun close() = lifecycles.parTraverse {
        it.stop(1000, "Normal closing")
    }

    private suspend fun <P : IPayload, C : Interceptor.Context, I : Interceptor<C>, E> processIncomingAsync(
        payload: Payload<*>,
        interceptors: List<I>,
        transformer: Transformer<Payload<P>, E>,
        interceptorContext: (response: E) -> C
    ) = CoroutineScope(Dispatchers.Unconfined).async {
        val incoming = transformer.transformIncoming(payload as Payload<P>) ?: return@async
        interceptors.parTraverse { it.processIncoming(payload, interceptorContext(incoming)) }
    }

    private suspend fun <P : IPayload, E> Transformer<Payload<P>, E>.transformIncoming(
        payload: Payload<P>
    ) = Either.catch { transform(payload) }.handleErrorWith {
        mainLogger.error(it) { "Error happened while transforming ${payload.name}#${payload.opcode()}" }
        mainLogger.debug { payload.rawData }
        it.left()
    }.orNull()

    private suspend fun <C : Interceptor.Context> Interceptor<C>.processIncoming(payload: Payload<*>, context: C) =
        Either.catch { intercept(context) }.getOrHandle { t ->
            val logger = loggers[name] ?: KotlinLogging.logger { }
            logger.error(t) { "Unexpected fail on interceptor while processing ${payload.name}#${payload.opcode()}" }
            logger.debug { payload.rawData }
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