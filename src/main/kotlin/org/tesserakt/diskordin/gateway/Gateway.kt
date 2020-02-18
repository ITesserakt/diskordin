package org.tesserakt.diskordin.gateway

import arrow.syntax.function.memoize
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import okhttp3.OkHttpClient
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor
import org.tesserakt.diskordin.gateway.interceptor.TokenInterceptor
import org.tesserakt.diskordin.gateway.json.IRawEvent
import org.tesserakt.diskordin.gateway.json.IToken
import org.tesserakt.diskordin.gateway.json.Payload
import org.tesserakt.diskordin.gateway.shard.ShardController
import org.tesserakt.diskordin.gateway.transformer.RawEventTransformer
import org.tesserakt.diskordin.gateway.transformer.RawTokenTransformer
import org.tesserakt.diskordin.gateway.transformer.WebSocketEventTransformer
import org.tesserakt.diskordin.impl.core.client.setupScarlet

internal var sequenceId: Int? = null

class Gateway(
    private val context: BootstrapContext.Gateway,
    private val connections: List<GatewayConnection>
) {

    @FlowPreview
    @ExperimentalCoroutinesApi
    @Suppress("UNCHECKED_CAST")
    internal fun run(): Job {
        context.lifecycleRegistry.start()
        val controller = ShardController(
            context.connectionContext.shardSettings,
            connections
        )

        val transformed = connections.mapIndexed { index, c -> index to c.receive().asFlow() }.asFlow()
        val chain = transformed.flatMapMerge { (index, connection) ->
            connection.map { WebSocketEventTransformer.transform(it) }
                .onEach { sequenceId = it.seq ?: sequenceId }
                .flowOn(context.scheduler)
                .flatMapMerge { payload ->
                    if (payload.isTokenPayload) {
                        val token = RawTokenTransformer.transform(payload as Payload<IToken>)
                        val interceptorContext = TokenInterceptor.Context(connections[index], token, controller, index)
                        context.interceptors
                            .filter { it.selfContext == TokenInterceptor.Context::class }
                            .map { it.intercept(interceptorContext) }
                    } else {
                        val event = RawEventTransformer.transform(payload as Payload<IRawEvent>)
                        val interceptorContext = EventInterceptor.Context(connections[index], event, controller, index)
                        context.interceptors
                            .filter { it.selfContext == EventInterceptor.Context::class }
                            .map { it.intercept(interceptorContext) }
                    }
                }
        }

        return CoroutineScope(context.scheduler).launch {
            chain.retry { it !is CancellationException }.collect()
        }
    }

    companion object Factory {
        private const val gatewayVersion = 6
        private const val encoding = "json"

        private val gatewayUrl = { start: String, compression: String ->
            "$start/?v=$gatewayVersion&encoding=$encoding&compression=$compression"
        }.memoize()

        private val scarlet = { start: String, compression: String, httpClient: OkHttpClient ->
            setupScarlet(gatewayUrl(start, compression), httpClient)
        }

        private val impl = { context: BootstrapContext.Gateway.Connection ->
            scarlet(context.url, context.compression, context.httpClient).create<GatewayConnection>()
        }

        private val connections = { context: BootstrapContext.Gateway.Connection ->
            (0 until context.shardSettings.shardCount).map { impl(context) }
        }

        fun create(context: BootstrapContext.Gateway) = Gateway(context, connections(context.connectionContext))
    }
}