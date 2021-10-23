package org.tesserakt.diskordin.gateway

import arrow.fx.coroutines.parTraverse
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import mu.KotlinLogging
import org.tesserakt.diskordin.core.client.BootstrapContext
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
import kotlin.coroutines.CoroutineContext

@Suppress("UNCHECKED_CAST")
class Gateway(
    interceptors: List<Interceptor<*>>,
    gatewayContext: CoroutineContext,
    private val shardContext: ShardContext,
    private val lifecycles: List<GatewayLifecycleManager>
) {
    private val tokenInterceptors = interceptors
        .filter { it.selfContext == TokenInterceptor.Context::class } as List<Interceptor<TokenInterceptor.Context>>

    private val eventInterceptors = interceptors
        .filter { it.selfContext == EventInterceptor.Context::class } as List<Interceptor<EventInterceptor.Context>>

    private val mainLogger = KotlinLogging.logger("[Gateway]")
    private val scope = CoroutineScope(gatewayContext
            + CoroutineName("[Gateway]") + CoroutineExceptionHandler { coroutineContext, throwable ->
        val coroutineName = coroutineContext[CoroutineName]
        mainLogger.error(throwable) { "Coroutine $coroutineName crashed" }
    })
    private val controller = ShardController(shardContext, lifecycles, scope)

    internal suspend fun run() =
        lifecycles.onEach { it.start() }.map { it to it.connection.receive() }.withIndex().map { (index, it) ->
            val (lifecycle, events) = it
            val shard = Shard(
                shardContext.token,
                Shard.Data(index, shardContext.shardCount.value()),
                lifecycle
            )

            events.map(WebSocketEventTransformer::transform)
                .onEach { shard._sequence.value = it.seq ?: shard.sequence.value }
                .map { payload ->
                    if (payload.isTokenPayload)
                        processIncoming(payload, tokenInterceptors, RawTokenTransformer) {
                            TokenInterceptor.Context(it, controller, shard)
                        }
                    else processIncoming(payload, eventInterceptors, RawEventTransformer) {
                        EventInterceptor.Context(it, controller, shard)
                    }
                }.launchIn(lifecycle.coroutineScope)
        }

    @ExperimentalCoroutinesApi
    internal suspend fun close() {
        lifecycles.parTraverse { it.stop(1000, "Normal closing") }
        scope.cancel()
    }

    private suspend fun <P : IPayload, C : Interceptor.Context, I : Interceptor<C>, E> processIncoming(
        payload: Payload<*>,
        interceptors: List<I>,
        transformer: Transformer<Payload<P>, E>,
        interceptorContext: (response: E) -> C
    ) {
        val incoming = transformer.transform(payload as Payload<P>)
        interceptors.map { it.interceptWithJob(interceptorContext(incoming)) }
    }

    abstract class Factory {
        private val gatewayVersion = 9
        private val encoding = "json"

        protected val gatewayUrl = { start: String, compression: String ->
            "$start/?v=$gatewayVersion&encoding=$encoding&compression=$compression"
        }

        abstract fun BootstrapContext.createGateway(): Gateway
    }
}