package org.tesserakt.diskordin.gateway

import arrow.Kind
import arrow.core.FunctionK
import arrow.core.toT
import arrow.fx.typeclasses.Async
import okhttp3.OkHttpClient
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.core.client.GatewayLifecycleManager
import org.tesserakt.diskordin.gateway.interceptor.*
import org.tesserakt.diskordin.gateway.json.IPayload
import org.tesserakt.diskordin.gateway.json.IRawEvent
import org.tesserakt.diskordin.gateway.json.IToken
import org.tesserakt.diskordin.gateway.json.Payload
import org.tesserakt.diskordin.impl.core.client.setupScarlet
import kotlin.coroutines.CoroutineContext

typealias GatewayCompiler<G> = FunctionK<ForGatewayAPIF, G>

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE", "unused")
class Gateway(
    private val scheduler: CoroutineContext,
    private val lifecycleRegistry: GatewayLifecycleManager,
    private val interceptors: List<Interceptor<Interceptor.Context>>
) {
    @Suppress("UNCHECKED_CAST")
    @ExperimentalStdlibApi
    internal fun <G> run(compiler: GatewayCompiler<G>, A: Async<G>): Kind<G, Payload<out IPayload>> = A.run {
        lifecycleRegistry.start()

        observeWebSocketEvents()
            .map(WebSocketEventTransformer::transform)
            .foldMap(compiler, this)
            .continueOn(scheduler)
            .flatTap { payload ->
                effect {
                    if (payload.isTokenPayload) {
                        val token = RawTokenTransformer.transform(payload as Payload<IToken>)
                        interceptors
                            .filter { it.selfContext == TokenInterceptor.Context::class }
                            .forEach { it.intercept(TokenInterceptor.Context(token)) }
                    } else {
                        val event = RawEventTransformer.transform(payload as Payload<IRawEvent>)
                        interceptors
                            .filter { it.selfContext == EventInterceptor.Context::class }
                            .forEach { it.intercept(EventInterceptor.Context(event)) }
                    }
                }
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

        private val impl = { start: String, compression: String, httpClient: OkHttpClient ->
            scarlet(start, compression, httpClient).create<Implementation>()
        }

        fun create(context: BootstrapContext.Gateway) =
            Gateway(context.scheduler, context.lifecycleRegistry, context.interceptors) toT impl(
                context.connectionContext.url,
                context.connectionContext.compression,
                context.httpClient
            )
    }
}