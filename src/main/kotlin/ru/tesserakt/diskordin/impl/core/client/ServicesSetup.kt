package ru.tesserakt.diskordin.impl.core.client

import com.github.kittinunf.fuel.core.FuelManager
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.retry.ExponentialWithJitterBackoffStrategy
import com.tinder.scarlet.websocket.ShutdownReason
import com.tinder.scarlet.websocket.okhttp.OkHttpWebSocket
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.Logger.slf4jLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.util.FlowStreamAdapter
import ru.tesserakt.diskordin.util.FlowableStreamAdapter
import ru.tesserakt.diskordin.util.gson
import java.io.File

private val apiURL
    get() = GlobalContext.get().koin.getProperty<String>("API_url")
        ?: throw NoSuchElementException("There is no url to discord API. Please specify it in koin.properties file")

internal fun setupFuel(client: IDiscordClient) = FuelManager.instance.let {
    it.baseHeaders = mapOf(
        "Authorization" to "${client.tokenType} ${client.token}",
        "User-Agent" to "DiscordBot (Diskordin, 0.0.1)"
    )
    it.basePath = apiURL
}

internal fun setupKoin() = startKoin {
    fileProperties()
    slf4jLogger(Level.INFO)
    environmentProperties()
}.koin

internal fun setupHttpClient(): OkHttpClient = OkHttpClient().newBuilder()
    .cache(Cache(File.createTempFile("okHttpCache", null), 10 * 1024 * 1024))
    .build()

internal fun setupScarlet(path: String, httpClient: OkHttpClient): Scarlet {
    val protocol = OkHttpWebSocket(
        httpClient,
        OkHttpWebSocket.SimpleRequestFactory(
            { Request.Builder().url(path).build() },
            { ShutdownReason.GRACEFUL }
        )
    )
    val configuration = Scarlet.Configuration(
        backoffStrategy = ExponentialWithJitterBackoffStrategy(1000, 10000),
        streamAdapterFactories = listOf(FlowStreamAdapter.Factory(), FlowableStreamAdapter.Factory()),
        messageAdapterFactories = listOf(GsonMessageAdapter.Factory(gson))
    )

    return Scarlet(protocol, configuration)
}
