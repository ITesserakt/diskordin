package org.tesserakt.diskordin.gateway

import arrow.syntax.function.memoize
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import okhttp3.OkHttpClient
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor
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
class Gateway(
    private val context: BootstrapContext.Gateway,
    private val connections: List<GatewayConnection>
) {
    private val controller = ShardController(
        context.connectionContext.shardSettings,
        connections
    )

    @FlowPreview
    @ExperimentalCoroutinesApi
    internal fun run(): Job {
        context.lifecycleRegistry.start()

        val transformed = connections.mapIndexed { index, c -> index to c.receive().asFlow() }.asFlow()
        val chain = transformed.flatMapMerge { (index, connection) ->
            val shard = Shard(
                context.connectionContext.shardSettings.token,
                index to context.connectionContext.shardSettings.shardCount,
                connections[index]
            )

            connection.map { WebSocketEventTransformer.transform(it) }
                .flowOn(context.scheduler)
                .flatMapMerge { payload ->
                    if (payload.isTokenPayload) {
                        composeToken(payload, shard)
                    } else {
                        shard.sequence = payload.seq
                        composeEvent(payload, shard)
                    }
                }
        }

        return CoroutineScope(context.scheduler).launch {
            chain.retry { it !is CancellationException }.collect()
        }
    }

    private fun composeToken(
        payload: Payload<out IPayload>,
        shard: Shard
    ): Flow<Unit> {
        val token = RawTokenTransformer.transform(payload as Payload<IToken>)
        val interceptorContext = TokenInterceptor.Context(token, controller, shard)

        return context.interceptors
            .filter { it.selfContext == TokenInterceptor.Context::class }
            .map { it.intercept(interceptorContext) }
    }

    private fun composeEvent(
        payload: Payload<out IPayload>,
        shard: Shard
    ): Flow<Unit> {
        val event = RawEventTransformer.transform(payload as Payload<IRawEvent>)
        val interceptorContext = EventInterceptor.Context(event, controller, shard)

        return context.interceptors
            .filter { it.selfContext == EventInterceptor.Context::class }
            .map { it.intercept(interceptorContext) }
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