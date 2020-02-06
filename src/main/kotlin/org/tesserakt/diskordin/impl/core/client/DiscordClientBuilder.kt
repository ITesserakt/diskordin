package org.tesserakt.diskordin.impl.core.client

import arrow.fx.IO
import arrow.fx.extensions.io.async.async
import kotlinx.coroutines.Dispatchers
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.core.client.IDiscordClient
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IEntity
import org.tesserakt.diskordin.gateway.interceptor.Interceptor
import org.tesserakt.diskordin.gateway.sequenceId
import org.tesserakt.diskordin.impl.gateway.interceptor.HeartbeatACKInterceptor
import org.tesserakt.diskordin.impl.gateway.interceptor.HeartbeatInterceptor
import org.tesserakt.diskordin.impl.gateway.interceptor.HelloChain
import org.tesserakt.diskordin.impl.gateway.interceptor.WebSocketStateInterceptor
import org.tesserakt.diskordin.rest.RestClient
import org.tesserakt.diskordin.util.NoopMap
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

@Suppress("NOTHING_TO_INLINE", "unused")
class DiscordClientBuilder private constructor() {
    private var token: String = "Invalid"
    private var gatewayContext: CoroutineContext = Dispatchers.IO
    private var compression = ""
    private var cache: MutableMap<Snowflake, IEntity> = ConcurrentHashMap()
    private val interceptors = mutableListOf<Interceptor<Interceptor.Context>>()

    operator fun String.unaryPlus() {
        token = this
    }

    operator fun CoroutineContext.unaryPlus() {
        gatewayContext = this
    }

    operator fun Unit.unaryPlus() {
        compression = "zlib-stream"
    }

    operator fun Boolean.unaryPlus() {
        cache = if (this) ConcurrentHashMap()
        else NoopMap()
    }

    internal operator fun VerificationStub.unaryPlus() {
        token = "NTQ3NDg5MTA3NTg1MDA3NjM2.123456.123456789"
    }

    @Suppress("UNCHECKED_CAST")
    operator fun Interceptor<*>.unaryPlus() {
        interceptors.add(this as Interceptor<Interceptor.Context>)
    }

    inline fun DiscordClientBuilder.token(value: String) = value
    inline fun DiscordClientBuilder.useCompression() = Unit
    inline fun DiscordClientBuilder.context(coroutineContext: CoroutineContext) = coroutineContext
    inline fun DiscordClientBuilder.cache(value: Boolean) = value
    internal inline fun DiscordClientBuilder.disableTokenVerification() = VerificationStub
    inline fun DiscordClientBuilder.gatewayInterceptor(value: Interceptor<*>) = value
    inline fun <reified C : Interceptor.Context> DiscordClientBuilder.gatewayInterceptor(crossinline f: (C) -> Unit): Interceptor<*> {
        val interceptor = object : Interceptor<C> {
            override suspend fun intercept(context: C) = f(context)
            override val selfContext: KClass<C> = C::class
        }
        return gatewayInterceptor(interceptor)
    }

    internal object VerificationStub

    companion object {
        operator fun invoke(init: DiscordClientBuilder.() -> Unit = {}): IDiscordClient {
            val builder = DiscordClientBuilder().apply(init).apply {
                +gatewayInterceptor(WebSocketStateInterceptor())
                val helloChain = HelloChain(
                    compression != "",
                    System.getenv("token") ?: token,
                    ::sequenceId
                )
                +gatewayInterceptor(HeartbeatInterceptor(::sequenceId))
                +gatewayInterceptor(HeartbeatACKInterceptor())
                +gatewayInterceptor(helloChain.ConnectionInterceptor())
                +gatewayInterceptor(helloChain)
            }
            val token = System.getenv("token") ?: builder.token
            val httpClient = setupHttpClient(token)
            val retrofit = setupRetrofit("https://discordapp.com/api/v6/", httpClient)
            val rest = RestClient.byRetrofit(retrofit, IO.async())

            val connectionContext = BootstrapContext.Gateway.Connection(
                "wss://gateway.discord.gg",
                builder.compression
            )
            val gatewayContext = BootstrapContext.Gateway(
                builder.gatewayContext,
                httpClient,
                GlobalGatewayLifecycle,
                builder.interceptors,
                connectionContext
            )
            val globalContext = BootstrapContext(
                token,
                builder.cache,
                rest,
                gatewayContext
            )
            return DiscordClient(globalContext).unsafeRunSync()
        }
    }
}