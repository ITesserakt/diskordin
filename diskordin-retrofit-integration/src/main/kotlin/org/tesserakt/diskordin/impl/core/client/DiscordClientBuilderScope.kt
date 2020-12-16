package org.tesserakt.diskordin.impl.core.client

import arrow.core.Eval
import com.tinder.scarlet.retry.BackoffStrategy
import com.tinder.scarlet.retry.ExponentialBackoffStrategy
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.gateway.Gateway
import org.tesserakt.diskordin.gateway.integration.ScarletFactory
import org.tesserakt.diskordin.rest.RestClient
import org.tesserakt.diskordin.rest.RetrofitRestClient
import org.tesserakt.diskordin.rest.integration.RetrofitService
import java.util.*
import java.util.concurrent.TimeUnit

inline class HttpClient(private val inner: Eval<OkHttpClient>) : BootstrapContext.ExtensionContext {
    companion object : BootstrapContext.PersistentExtension<HttpClient>

    operator fun invoke() = inner.extract()
}

@Suppress("NOTHING_TO_INLINE", "unused")
class RetrofitScope : DiscordClientBuilderScope() {
    override lateinit var restClient: RestClient private set
    private var okHttpClient: Eval<OkHttpClient> = overrideHttpClient { defaultHttpClient(logger) }
    private var logger: (String) -> Unit = ::println
    private var webSocketDebug: Boolean = false
    private var backoffStrategy: BackoffStrategy = ExponentialBackoffStrategy(1000, 10000)
    override lateinit var gatewayFactory: Gateway.Factory

    override fun create(): DiscordClientSettings {
        val token = System.getenv("DISKORDIN_TOKEN") ?: token ?: error(DiscordClientBuilder.NoTokenProvided)
        +refineHttpClient { addInterceptor(AuthorityInterceptor(token)) }
        +install(HttpClient) { HttpClient(okHttpClient) }
        val retrofit by RetrofitService(okHttpClient, discordApiUrl)
        restClient = RetrofitRestClient(retrofit::extract, restSchedule)
        gatewayFactory = ScarletFactory(okHttpClient, backoffStrategy, gatewaySettings.coroutineContext, webSocketDebug)

        return DiscordClientSettings(
            token,
            cache,
            gatewaySettings,
            restSchedule,
            restClient,
            gatewayFactory,
            extensions
        )
    }

    operator fun Eval<OkHttpClient>.unaryPlus() {
        okHttpClient = this
    }

    operator fun ((String) -> Unit).unaryPlus() {
        logger = this
    }

    operator fun Boolean.unaryPlus() {
        webSocketDebug = this
    }

    operator fun BackoffStrategy.unaryPlus() {
        backoffStrategy = this
    }

    inline fun RetrofitScope.logger(noinline value: (String) -> Unit) = value
    inline fun RetrofitScope.enableScarletDebug() = true
    inline fun RetrofitScope.backoffStrategy(value: BackoffStrategy) = value

    fun RetrofitScope.refineHttpClient(block: OkHttpClient.Builder.() -> OkHttpClient.Builder) =
        overrideHttpClient(okHttpClient.map { it.newBuilder().block().build() })

    inline fun RetrofitScope.overrideHttpClient(crossinline client: () -> OkHttpClient) =
        overrideHttpClient(Eval.later(client))

    inline fun RetrofitScope.overrideHttpClient(client: Eval<OkHttpClient>) = client

    private fun defaultHttpClient(logger: (String) -> Unit) = OkHttpClient().newBuilder()
        .retryOnConnectionFailure(true)
        .connectTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) = logger(message)
            }).setLevel(HttpLoggingInterceptor.Level.BASIC)
        ).build()
}

class AuthorityInterceptor(private val token: String) : Interceptor {
    companion object {
        @JvmStatic
        private val diskordinVersion = try {
            PropertyResourceBundle.getBundle("gradle.properties").getString("diskordin_version")
        } catch (e: Throwable) {
            "test environment"
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bot $token")
            .addHeader("User-Agent", "Discord bot (Diskordin $diskordinVersion)")
            .build()

        return chain.proceed(request)
    }
}