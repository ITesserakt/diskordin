package org.tesserakt.diskordin.impl.core.client

import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.async.async
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import okhttp3.OkHttpClient
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.core.client.IDiscordClient
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.UserStatusUpdateRequest
import org.tesserakt.diskordin.core.entity.IEntity
import org.tesserakt.diskordin.core.entity.builder.PresenceBuilder
import org.tesserakt.diskordin.core.entity.builder.RequestBuilder
import org.tesserakt.diskordin.gateway.interceptor.Interceptor
import org.tesserakt.diskordin.gateway.sequenceId
import org.tesserakt.diskordin.gateway.shard.*
import org.tesserakt.diskordin.impl.gateway.interceptor.*
import org.tesserakt.diskordin.rest.RestClient
import org.tesserakt.diskordin.util.NoopMap
import org.tesserakt.diskordin.util.enums.IValued
import org.tesserakt.diskordin.util.enums.ValuedEnum
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

inline class ShardCount(val v: Int)

@RequestBuilder
@Suppress("NOTHING_TO_INLINE", "unused")
class DiscordClientBuilder private constructor() {
    private var token: String = "Invalid"
    private var gatewayContext: CoroutineContext = Dispatchers.IO
    private var compression = ""
    private var cache: MutableMap<Snowflake, IEntity> = ConcurrentHashMap()
    private val interceptors = mutableListOf<Interceptor<Interceptor.Context>>()
    private var guildSubscriptionsStrategy: GuildSubscriptionsStrategy =
        GuildSubscriptionsStrategy.SubscribeTo(emptyList())
    private var compressionStrategy: CompressionStrategy = CompressionStrategy.CompressOnly(emptyList())
    private var shardCount = 1
    private var threshold = ShardThreshold(emptyMap())
    private var request: UserStatusUpdateRequest? = null
    private var intents: IntentsStrategy = IntentsStrategy.EnableAll

    operator fun String.unaryPlus() {
        token = this
    }

    operator fun CoroutineContext.unaryPlus() {
        gatewayContext = this
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

    operator fun List<Int>.unaryPlus() {
        guildSubscriptionsStrategy = if (this.isNotEmpty())
            GuildSubscriptionsStrategy.SubscribeTo(this)
        else
            GuildSubscriptionsStrategy.SubscribeToAll
    }

    operator fun IntArray.unaryPlus() {
        compression = "zlib-stream"
        compressionStrategy = if (this.isNotEmpty())
            CompressionStrategy.CompressOnly(this.toList())
        else
            CompressionStrategy.CompressAll
    }

    operator fun ShardCount.unaryPlus() {
        shardCount = this.v
    }

    operator fun Map<Int, Int>.unaryPlus() {
        require(this.values.all { it in 50..250 }) {
            "Invalid threshold value. It should be in the range [50; 250]"
        }
        require(this.keys.all { it < shardCount }) {
            "Invalid shard index. It should be less than shard count"
        }
        threshold = ShardThreshold(this)
    }

    operator fun PresenceBuilder.unaryPlus() {
        this@DiscordClientBuilder.request = this@unaryPlus.create()
    }

    operator fun Pair<Int, Short>.unaryPlus() {
        intents = when (intents) {
            IntentsStrategy.EnableAll -> IntentsStrategy.EnableOnly(mapOf(this))
            is IntentsStrategy.EnableOnly -> {
                val saved = (intents as IntentsStrategy.EnableOnly).enabled
                IntentsStrategy.EnableOnly(saved + this)
            }
        }
    }

    inline fun DiscordClientBuilder.token(value: String) = value
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

    inline fun DiscordClientBuilder.guildSubscriptions(vararg shardIndexes: Int) = shardIndexes.toList()
    inline fun DiscordClientBuilder.compressShards(vararg shardIndexes: Int) = shardIndexes
    inline fun DiscordClientBuilder.useShards(count: Int) = ShardCount(count)
    inline fun DiscordClientBuilder.thresholdOverrides(vararg overrides: Pair<ShardIndex, LargeThreshold>) =
        overrides.toMap()

    inline fun DiscordClientBuilder.initialPresence(builder: PresenceBuilder.() -> Unit) =
        PresenceBuilder().apply(builder)

    inline fun DiscordClientBuilder.featureOverrides(shardIndex: Int, value: ValuedEnum<Intents, Short>) =
        shardIndex to value.code

    inline fun DiscordClientBuilder.featureOverrides(shardIndex: Int, value: IValued<Intents, Short>) =
        shardIndex to value.value

    internal object VerificationStub

    companion object {
        operator fun invoke(init: DiscordClientBuilder.() -> Unit = {}): IDiscordClient {
            val builder = DiscordClientBuilder().apply(init).apply {
                +gatewayInterceptor(WebSocketStateInterceptor())
                val helloChain = HelloChain()
                +gatewayInterceptor(HeartbeatInterceptor(::sequenceId))
                +gatewayInterceptor(HeartbeatACKInterceptor())
                +gatewayInterceptor(helloChain.ConnectionInterceptor())
                +gatewayInterceptor(helloChain)
                +gatewayInterceptor(ShardApprover())
            }
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
            builder.cache,
            rest,
            gatewayContext
        )

        private fun formGatewaySettings(
            builder: DiscordClientBuilder,
            connectionContext: BootstrapContext.Gateway.Connection
        ): BootstrapContext.Gateway = BootstrapContext.Gateway(
            builder.gatewayContext,
            GlobalGatewayLifecycle,
            builder.interceptors.asFlow(),
            connectionContext
        )

        private fun formConnectionSettings(
            httpClient: OkHttpClient,
            builder: DiscordClientBuilder,
            shardSettings: BootstrapContext.Gateway.Connection.ShardSettings
        ): BootstrapContext.Gateway.Connection = BootstrapContext.Gateway.Connection(
            httpClient,
            "wss://gateway.discord.gg",
            builder.compression,
            shardSettings
        )

        private fun formShardSettings(
            token: String,
            builder: DiscordClientBuilder
        ): BootstrapContext.Gateway.Connection.ShardSettings = BootstrapContext.Gateway.Connection.ShardSettings(
            token,
            builder.shardCount,
            builder.compressionStrategy,
            builder.guildSubscriptionsStrategy,
            builder.threshold,
            builder.request,
            builder.intents
        )
    }
}