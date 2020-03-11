package org.tesserakt.diskordin.impl.core.client

import arrow.core.Eval
import arrow.fx.IO
import arrow.fx.extensions.io.async.async
import arrow.syntax.function.partially1
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.core.client.IDiscordClient
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IEntity
import org.tesserakt.diskordin.rest.RestClient
import org.tesserakt.diskordin.util.NoopMap
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

@Suppress("NOTHING_TO_INLINE", "unused")
class DiscordClientBuilder private constructor() {
    private var token: String = "Invalid"
    private var gatewayContext: CoroutineContext = Dispatchers.IO
    private var compression = ""
    private var cache: MutableMap<Snowflake, IEntity> = ConcurrentHashMap()
    private var httpClient: Eval<OkHttpClient> = Eval.later(::defaultHttpClient)

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

    operator fun Eval<OkHttpClient>.unaryPlus() {
        httpClient = this
    }

    inline fun DiscordClientBuilder.token(value: String) = value
    inline fun DiscordClientBuilder.useCompression() = Unit
    inline fun DiscordClientBuilder.context(coroutineContext: CoroutineContext) = coroutineContext
    inline fun DiscordClientBuilder.cache(value: Boolean) = value
    internal inline fun DiscordClientBuilder.disableTokenVerification() = VerificationStub
    inline fun DiscordClientBuilder.overrideHttpClient(client: Eval<OkHttpClient>) = client
    inline fun DiscordClientBuilder.overrideHttpClient(noinline client: () -> OkHttpClient): Eval<OkHttpClient> =
        Eval.later(client)

    internal object VerificationStub

    companion object {
        operator fun invoke(init: DiscordClientBuilder.() -> Unit = {}): IDiscordClient {
            val builder = DiscordClientBuilder().apply(init)

            val token = System.getenv("token") ?: builder.token
            val httpClient = builder.httpClient.map {
                it.newBuilder()
                    .addInterceptor(AuthorityInterceptor(token))
                    .build()
            }
            val retrofit = httpClient.map(::setupRetrofit.partially1("https://discordapp.com/api/v6/"))
            val rest = retrofit.map { RestClient.byRetrofit(it, IO.async()) }

            val connectionContext = BootstrapContext.Gateway.Connection(
                "wss://gateway.discord.gg",
                builder.compression
            )
            val gatewayContext = BootstrapContext.Gateway(
                builder.gatewayContext,
                httpClient,
                GlobalGatewayLifecycle,
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