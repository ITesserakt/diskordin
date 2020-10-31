package org.tesserakt.diskordin.impl.core.client

import arrow.core.Eval
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.tesserakt.diskordin.rest.RestClient
import org.tesserakt.diskordin.rest.RetrofitRestClient
import org.tesserakt.diskordin.rest.integration.RetrofitService
import java.util.*
import java.util.concurrent.TimeUnit

@Suppress("NOTHING_TO_INLINE", "unused")
class RetrofitScope : DiscordClientBuilderScope() {
    override lateinit var restClient: RestClient
        private set
    private var okHttpClient: Eval<OkHttpClient> = overrideHttpClient { defaultHttpClient(logger) }
    private var logger: (String) -> Unit = { }

    override fun DiscordClientBuilder.finalize(): DiscordClientBuilderScope = this@RetrofitScope.apply {
        val retrofit by RetrofitService(okHttpClient, discordApiUrl)
        restClient = RetrofitRestClient(retrofit::extract, restSchedule)
    }

    operator fun Eval<OkHttpClient>.unaryPlus() {
        okHttpClient = this
    }

    operator fun ((String) -> Unit).unaryPlus() {
        logger = this
    }

    inline fun RetrofitScope.logger(noinline value: (String) -> Unit) = value

    fun RetrofitScope.refineHttpClient(block: OkHttpClient.Builder.() -> OkHttpClient.Builder) =
        overrideHttpClient(okHttpClient.map { it.newBuilder().block().build() })

    inline fun RetrofitScope.overrideHttpClient(crossinline client: () -> OkHttpClient) =
        overrideHttpClient(Eval.later(client))

    inline fun RetrofitScope.overrideHttpClient(client: Eval<OkHttpClient>) =
        client.map {
            it.newBuilder().apply {
                if (AuthorityInterceptor::class !in interceptors().map { interceptor -> interceptor::class })
                    addInterceptor(AuthorityInterceptor(token ?: error("No token provided at this moment")))
            }.build()
        }.memoize()

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