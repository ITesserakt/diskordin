package org.tesserakt.diskordin.impl.core.client

import arrow.integrations.retrofit.adapter.CallKindAdapterFactory
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.retry.ExponentialWithJitterBackoffStrategy
import com.tinder.scarlet.websocket.ShutdownReason
import com.tinder.scarlet.websocket.okhttp.OkHttpWebSocket
import mu.KotlinLogging
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.tesserakt.diskordin.util.gson
import org.tesserakt.diskordin.util.typeAdapter.SnowflakeTypeAdapter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

internal fun setupRetrofit(discordApiUrl: String, httpClient: OkHttpClient) = Retrofit.Builder()
    .client(httpClient)
    .baseUrl(discordApiUrl)
    .addConverterFactory(GsonConverterFactory.create(gson))
    .addConverterFactory(SnowflakeTypeAdapter())
    .addCallAdapterFactory(CallKindAdapterFactory.create())
    .build()

internal fun defaultHttpClient() = OkHttpClient().newBuilder()
    .retryOnConnectionFailure(true)
    .connectTimeout(20, TimeUnit.SECONDS)
    .writeTimeout(20, TimeUnit.SECONDS)
    .readTimeout(20, TimeUnit.SECONDS)
    .addInterceptor(
        HttpLoggingInterceptor(KotlinLogging.logger("[HTTP client]")::debug)
            .setLevel(HttpLoggingInterceptor.Level.BASIC)
    ).build()

internal fun setupScarlet(path: String, lifecycle: Lifecycle, httpClient: OkHttpClient): Scarlet {
    val protocol = OkHttpWebSocket(
        httpClient,
        OkHttpWebSocket.SimpleRequestFactory(
            { Request.Builder().url(path).build() },
            { ShutdownReason.GRACEFUL }
        )
    )
    val configuration = Scarlet.Configuration(
        backoffStrategy = ExponentialWithJitterBackoffStrategy(1000, 5000),
        lifecycle = lifecycle
    )

    return Scarlet(protocol, configuration)
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
