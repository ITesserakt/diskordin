package org.tesserakt.diskordin.impl.core.client

import arrow.fx.IO
import arrow.fx.extensions.io.async.async
import kotlinx.coroutines.Dispatchers
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.core.client.IDiscordClient
import org.tesserakt.diskordin.rest.RestClient
import kotlin.coroutines.CoroutineContext

@Suppress("NOTHING_TO_INLINE", "unused")
class DiscordClientBuilder private constructor() {
    private var token: String = "Invalid"
    private var gatewayContext: CoroutineContext = Dispatchers.IO
    private var compression = ""
    private var isCachesEnabled = true //TODO

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
        isCachesEnabled = this
    }

    inline fun DiscordClientBuilder.token(value: String) = value
    inline fun DiscordClientBuilder.useCompression() = Unit
    inline fun DiscordClientBuilder.context(coroutineContext: CoroutineContext) = coroutineContext
    inline fun DiscordClientBuilder.cache(value: Boolean) = value

    companion object {
        operator fun invoke(init: DiscordClientBuilder.() -> Unit): IDiscordClient {
            val builder = DiscordClientBuilder().apply(init)

            val token = System.getenv("token") ?: builder.token
            val libraryVersion = System.getenv("diskordin_version")
            val httpClient = setupHttpClient(libraryVersion, token)
            val retrofit = setupRetrofit("https://discordapp.com/api/v6/", httpClient)
            val rest = RestClient(retrofit, IO.async())

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
                builder.isCachesEnabled,
                rest,
                gatewayContext
            )
            return DiscordClient(globalContext).unsafeRunSync()
        }
    }
}