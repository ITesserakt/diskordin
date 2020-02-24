package org.tesserakt.diskordin.impl.core.client

import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.async.async
import kotlinx.coroutines.flow.asFlow
import okhttp3.OkHttpClient
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.core.client.IDiscordClient
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IEntity
import org.tesserakt.diskordin.core.entity.builder.RequestBuilder
import org.tesserakt.diskordin.gateway.shard.IntentsStrategy
import org.tesserakt.diskordin.rest.RestClient
import org.tesserakt.diskordin.util.NoopMap
import java.util.concurrent.ConcurrentHashMap

inline class ShardCount(val v: Int)

@RequestBuilder
@Suppress("NOTHING_TO_INLINE", "unused")
class DiscordClientBuilder private constructor() {
    private var token: String = "Invalid"
    private var cache: MutableMap<Snowflake, IEntity> = ConcurrentHashMap()
    private var gatewaySettings: GatewayBuilder.GatewaySettings = GatewayBuilder().create()
    private var isIntentsEnabled = false

    operator fun String.unaryPlus() {
        token = this
    }

    operator fun Boolean.unaryPlus() {
        cache = if (this) ConcurrentHashMap()
        else NoopMap()
    }

    internal operator fun VerificationStub.unaryPlus() {
        token = "NTQ3NDg5MTA3NTg1MDA3NjM2.123456.123456789"
    }

    operator fun GatewayBuilder.unaryPlus() {
        this@DiscordClientBuilder.gatewaySettings = this.create()
        if (this@DiscordClientBuilder.gatewaySettings.intents is IntentsStrategy.EnableOnly)
            this@DiscordClientBuilder.isIntentsEnabled = true
    }

    inline fun DiscordClientBuilder.token(value: String) = value
    inline fun DiscordClientBuilder.cache(value: Boolean) = value
    internal inline fun DiscordClientBuilder.disableTokenVerification() = VerificationStub
    inline fun DiscordClientBuilder.gatewaySettings(f: GatewayBuilder.() -> Unit) = GatewayBuilder().apply(f)

    internal object VerificationStub

    companion object {
        operator fun invoke(init: DiscordClientBuilder.() -> Unit = {}): IDiscordClient {
            val builder = DiscordClientBuilder().apply(init)
            val token = System.getenv("token") ?: builder.token
            val httpClient = setupHttpClient(token)
            val retrofit = setupRetrofit("https://discordapp.com/api/v6/", httpClient)
            val rest = RestClient.byRetrofit(retrofit, IO.async())

            val shardSettings = formShardSettings(token, builder)
            val connectionContext = formConnectionSettings(httpClient, builder, shardSettings)
            val gatewayContext = formGatewaySettings(builder, connectionContext)
            val globalContext = formBootstrapContext(builder, rest, gatewayContext)
            return DiscordClient(globalContext).unsafeRunSync()
        }

        private fun formBootstrapContext(
            builder: DiscordClientBuilder,
            rest: RestClient<ForIO>,
            gatewayContext: BootstrapContext.Gateway
        ): BootstrapContext<ForIO> = BootstrapContext(
            if (builder.isIntentsEnabled) NoopMap() else builder.cache,
            rest,
            gatewayContext
        )

        private fun formGatewaySettings(
            builder: DiscordClientBuilder,
            connectionContext: BootstrapContext.Gateway.Connection
        ): BootstrapContext.Gateway = BootstrapContext.Gateway(
            builder.gatewaySettings.coroutineContext,
            builder.gatewaySettings.interceptors.asFlow(),
            connectionContext
        )

        private fun formConnectionSettings(
            httpClient: OkHttpClient,
            builder: DiscordClientBuilder,
            shardSettings: BootstrapContext.Gateway.Connection.ShardSettings
        ): BootstrapContext.Gateway.Connection = BootstrapContext.Gateway.Connection(
            httpClient,
            "wss://gateway.discord.gg",
            builder.gatewaySettings.compression,
            shardSettings
        )

        private fun formShardSettings(
            token: String,
            builder: DiscordClientBuilder
        ): BootstrapContext.Gateway.Connection.ShardSettings = BootstrapContext.Gateway.Connection.ShardSettings(
            token,
            builder.gatewaySettings.shardCount,
            builder.gatewaySettings.compressionStrategy,
            builder.gatewaySettings.guildSubscriptionsStrategy,
            builder.gatewaySettings.threshold,
            builder.gatewaySettings.initialPresence,
            builder.gatewaySettings.intents
        )
    }
}