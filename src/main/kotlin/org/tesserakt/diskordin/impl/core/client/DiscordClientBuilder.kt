package org.tesserakt.diskordin.impl.core.client

import arrow.Kind
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.async.async
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.fix
import arrow.fx.typeclasses.Concurrent
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
class DiscordClientBuilder<F> private constructor(val CC: Concurrent<F>) {
    private var token: String = "Invalid"
    private var cache: MutableMap<Snowflake, IEntity> = ConcurrentHashMap()
    private var gatewaySettings: GatewayBuilder.GatewaySettings<F> = GatewayBuilder(CC).create()
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

    operator fun GatewayBuilder<F>.unaryPlus() {
        this@DiscordClientBuilder.gatewaySettings = this.create()
        if (this@DiscordClientBuilder.gatewaySettings.intents is IntentsStrategy.EnableOnly)
            this@DiscordClientBuilder.isIntentsEnabled = true
    }

    inline fun DiscordClientBuilder<F>.token(value: String) = value
    inline fun DiscordClientBuilder<F>.cache(value: Boolean) = value
    internal inline fun DiscordClientBuilder<F>.disableTokenVerification() = VerificationStub
    inline fun <F> DiscordClientBuilder<F>.gatewaySettings(f: GatewayBuilder<F>.() -> Unit) =
        GatewayBuilder(CC).apply(f)

    internal object VerificationStub

    companion object {
        operator fun <F> invoke(
            CC: Concurrent<F>,
            effectRunner: suspend (Kind<F, *>) -> Unit,
            init: DiscordClientBuilder<F>.() -> Unit = {}
        ): IDiscordClient {
            val builder = DiscordClientBuilder(CC).apply(init)
            val token = System.getenv("token") ?: builder.token
            val httpClient = setupHttpClient(token)
            val retrofit = setupRetrofit("https://discordapp.com/api/v6/", httpClient)
            val rest = RestClient.byRetrofit(retrofit, IO.async())

            val shardSettings = formShardSettings(token, builder)
            val connectionContext = formConnectionSettings(httpClient, builder, shardSettings)
            val gatewayContext = formGatewaySettings(builder, CC, effectRunner, connectionContext)
            val globalContext = formBootstrapContext(builder, rest, gatewayContext)
            return DiscordClient(globalContext).unsafeRunSync()
        }

        private fun <F> formBootstrapContext(
            builder: DiscordClientBuilder<F>,
            rest: RestClient<ForIO>,
            gatewayContext: BootstrapContext.Gateway<F>
        ): BootstrapContext<ForIO, F> = BootstrapContext(
            if (builder.isIntentsEnabled) NoopMap() else builder.cache,
            rest,
            gatewayContext
        )

        private fun <F> formGatewaySettings(
            builder: DiscordClientBuilder<F>,
            CC: Concurrent<F>,
            runner: suspend (Kind<F, *>) -> Unit,
            connectionContext: BootstrapContext.Gateway.Connection
        ): BootstrapContext.Gateway<F> = BootstrapContext.Gateway(
            builder.gatewaySettings.coroutineContext,
            builder.gatewaySettings.interceptors,
            CC,
            runner,
            connectionContext
        )

        private fun <F> formConnectionSettings(
            httpClient: OkHttpClient,
            builder: DiscordClientBuilder<F>,
            shardSettings: BootstrapContext.Gateway.Connection.ShardSettings
        ): BootstrapContext.Gateway.Connection = BootstrapContext.Gateway.Connection(
            httpClient,
            "wss://gateway.discord.gg",
            builder.gatewaySettings.compression,
            shardSettings
        )

        private fun <F> formShardSettings(
            token: String,
            builder: DiscordClientBuilder<F>
        ): BootstrapContext.Gateway.Connection.ShardSettings = BootstrapContext.Gateway.Connection.ShardSettings(
            token,
            builder.gatewaySettings.shardCount,
            builder.gatewaySettings.compressionStrategy,
            builder.gatewaySettings.guildSubscriptionsStrategy,
            builder.gatewaySettings.threshold,
            builder.gatewaySettings.initialPresence,
            builder.gatewaySettings.intents
        )

        inline fun default(noinline init: DiscordClientBuilder<ForIO>.() -> Unit) =
            invoke(IO.concurrent(), { it.fix().suspended() }, init)
    }
}