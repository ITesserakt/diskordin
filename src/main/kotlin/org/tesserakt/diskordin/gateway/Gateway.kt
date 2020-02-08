package org.tesserakt.diskordin.gateway

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
import org.tesserakt.diskordin.gateway.transformer.RawEventTransformer
import org.tesserakt.diskordin.gateway.transformer.RawTokenTransformer
import org.tesserakt.diskordin.gateway.transformer.WebSocketEventTransformer
import org.tesserakt.diskordin.impl.core.client.setupScarlet

internal var sequenceId: Int? = null

class Gateway(
    private val context: BootstrapContext.Gateway,
    private val implementation: Implementation
) {
    @FlowPreview
    @ExperimentalCoroutinesApi
    @Suppress("UNCHECKED_CAST")
    internal fun run(): Job {
        context.lifecycleRegistry.start()
        val controller = ShardController(
            context.connectionContext.shardSettings,
            implementation
        )

        val chain = implementation.receive().asFlow()
            .map { WebSocketEventTransformer.transform(it) }
            .onEach { sequenceId = it.seq ?: sequenceId }
            .flowOn(context.scheduler)
            .flatMapMerge { payload ->
                if (payload.isTokenPayload) {
                    val token = RawTokenTransformer.transform(payload as Payload<IToken>)
                    context.interceptors
                        .filter { it.selfContext == TokenInterceptor.Context::class }
                        .map { it.intercept(TokenInterceptor.Context(implementation, token, controller)) }
                } else {
                    val event = RawEventTransformer.transform(payload as Payload<IRawEvent>)
                    context.interceptors
                        .filter { it.selfContext == EventInterceptor.Context::class }
                        .map { it.intercept(EventInterceptor.Context(implementation, event, controller)) }
                }
            }

        return CoroutineScope(context.scheduler).launch {
            chain.collect()
        }
    }

    companion object Factory {
        private const val gatewayVersion = 6
        private const val encoding = "json"

        private val gatewayUrl = { start: String, compression: String ->
            "$start/?v=$gatewayVersion&encoding=$encoding&compression=$compression"
        }

        private val scarlet = { start: String, compression: String, httpClient: OkHttpClient ->
            setupScarlet(gatewayUrl(start, compression), httpClient)
        }

        private val impl = { context: BootstrapContext.Gateway.Connection ->
            scarlet(context.url, context.compression, context.httpClient).create<Implementation>()
        }

        fun create(context: BootstrapContext.Gateway) = Gateway(context, impl(context.connectionContext))
    }
}