package ru.tesserakt.diskordin.impl.core.client

import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.LifecycleRegistry
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.retry.ExponentialWithJitterBackoffStrategy
import com.tinder.scarlet.websocket.ShutdownReason
import com.tinder.scarlet.websocket.okhttp.OkHttpWebSocket
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.Logger.slf4jLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.gateway.GatewayLifecycle
import ru.tesserakt.diskordin.util.FlowStreamAdapter
import ru.tesserakt.diskordin.util.Loggers
import ru.tesserakt.diskordin.util.SnowflakeTypeAdapter
import ru.tesserakt.diskordin.util.gson
import java.io.File
import java.util.concurrent.TimeUnit

internal fun setupRetrofit(discordApiUrl: String, httpClient: OkHttpClient) = Retrofit.Builder()
    .client(httpClient)
    .baseUrl(discordApiUrl)
    .addConverterFactory(GsonConverterFactory.create(gson))
    .addConverterFactory(SnowflakeTypeAdapter())
    .build()

internal fun setupKoin() = startKoin {
    fileProperties()
    slf4jLogger(Level.ERROR)
    environmentProperties()
}.koin

internal fun setupHttpClient(client: IDiscordClient): OkHttpClient = OkHttpClient().newBuilder()
    .cache(Cache(File.createTempFile("okHttpCache", null), 10 * 1024 * 1024))
    .retryOnConnectionFailure(true)
    .connectTimeout(20, TimeUnit.SECONDS)
    .writeTimeout(20, TimeUnit.SECONDS)
    .readTimeout(20, TimeUnit.SECONDS)
    .addInterceptor { chain ->
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "${client.tokenType} ${client.token}")
            .addHeader("User-Agent", "Discord bot (Diskordin, 0.0.1)")
            .build()
        chain.proceed(request)
    }.addInterceptor(
        HttpLoggingInterceptor(Loggers("[Http client]").value::debug)
            .setLevel(HttpLoggingInterceptor.Level.BASIC)
    ).build()

internal fun setupLifecycle(): Lifecycle = GatewayLifecycle(LifecycleRegistry())

internal fun setupScarlet(path: String, httpClient: OkHttpClient, lifecycle: Lifecycle): Scarlet {
    val protocol = OkHttpWebSocket(
        httpClient,
        OkHttpWebSocket.SimpleRequestFactory(
            { Request.Builder().url(path).build() },
            { ShutdownReason.GRACEFUL }
        )
    )
    val configuration = Scarlet.Configuration(
        backoffStrategy = ExponentialWithJitterBackoffStrategy(1000, 10000),
        streamAdapterFactories = listOf(FlowStreamAdapter.Factory()),
        messageAdapterFactories = listOf(GsonMessageAdapter.Factory(gson)),
        lifecycle = lifecycle
    )

    return Scarlet(protocol, configuration)
}
