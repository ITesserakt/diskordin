@file:Suppress("unused", "NOTHING_TO_INLINE")

package org.tesserakt.diskordin.impl.core.client

import arrow.fx.IO
import arrow.fx.extensions.io.environment.environment
import org.tesserakt.diskordin.core.data.json.request.JsonRequest
import org.tesserakt.diskordin.core.data.json.request.UserStatusUpdateRequest
import org.tesserakt.diskordin.core.entity.builder.BuilderBase
import org.tesserakt.diskordin.core.entity.builder.PresenceBuilder
import org.tesserakt.diskordin.core.entity.builder.RequestBuilder
import org.tesserakt.diskordin.gateway.interceptor.Interceptor
import org.tesserakt.diskordin.gateway.shard.*
import org.tesserakt.diskordin.impl.gateway.interceptor.*
import org.tesserakt.diskordin.util.enums.IValued
import org.tesserakt.diskordin.util.enums.ValuedEnum
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

@RequestBuilder
class GatewayBuilder : BuilderBase<GatewayBuilder.GatewaySettings>() {
    data class GatewaySettings(
        val coroutineContext: CoroutineContext,
        val compression: String,
        val interceptors: List<Interceptor<out Interceptor.Context>>,
        val guildSubscriptionsStrategy: GuildSubscriptionsStrategy,
        val compressionStrategy: CompressionStrategy,
        val shardCount: Int,
        val threshold: ShardThreshold,
        val initialPresence: UserStatusUpdateRequest?,
        val intents: IntentsStrategy,
        val url: String
    ) : JsonRequest()

    private var gatewayUrl = "wss://gateway.discord.gg"
    private var compression = ""
    private var gatewayContext: CoroutineContext = IO.environment().dispatchers().io()
    private val interceptors: MutableList<Interceptor<out Interceptor.Context>> = mutableListOf(
        WebSocketStateInterceptor(),
        HeartbeatInterceptor(),
        HeartbeatACKInterceptor(),
        HelloChain(),
        ShardApproval()
    )
    private var guildSubscriptionsStrategy: GuildSubscriptionsStrategy =
        GuildSubscriptionsStrategy.SubscribeTo(emptyList())
    private var compressionStrategy: CompressionStrategy = CompressionStrategy.CompressOnly(emptyList())
    private var shardCount = 0
    private var threshold = ShardThreshold(emptyMap())
    private var request: UserStatusUpdateRequest? = null
    private var intents: IntentsStrategy = IntentsStrategy.EnableAll

    @Suppress("UNCHECKED_CAST")
    operator fun Interceptor<*>.unaryPlus() {
        interceptors += this
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
        this@GatewayBuilder.request = this@unaryPlus.create()
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

    operator fun CoroutineContext.unaryPlus() {
        gatewayContext = this
    }

    operator fun String.unaryPlus() {
        gatewayUrl = this
    }

    inline fun GatewayBuilder.gatewayInterceptor(value: Interceptor<out Interceptor.Context>) = value
    inline fun <reified C : Interceptor.Context> GatewayBuilder.gatewayInterceptor(crossinline block: suspend (C) -> Unit) =
        object : Interceptor<C> {
            override val selfContext: KClass<C> = C::class

            override suspend fun intercept(context: C) = block(context)
        }

    inline fun GatewayBuilder.guildSubscriptions(vararg shardIndexes: Int) = shardIndexes.toList()
    inline fun GatewayBuilder.compressShards(vararg shardIndexes: Int) = shardIndexes
    inline fun GatewayBuilder.useShards(count: Int) = ShardCount(count)
    inline fun GatewayBuilder.thresholdOverrides(vararg overrides: Pair<ShardIndex, LargeThreshold>) =
        overrides.toMap()

    inline fun GatewayBuilder.initialPresence(builder: PresenceBuilder.() -> Unit) =
        PresenceBuilder().apply(builder)

    inline fun GatewayBuilder.featureOverrides(shardIndex: Int, value: ValuedEnum<Intents, Short>) =
        shardIndex to value.code

    inline fun GatewayBuilder.featureOverrides(shardIndex: Int, value: IValued<Intents, Short>) =
        shardIndex to value.code

    inline fun GatewayBuilder.coroutineContext(context: CoroutineContext) = context

    @DiscordClientBuilderScope.InternalTestAPI
    inline fun GatewayBuilder.websocketAddress(value: String) = value

    @Suppress("UNCHECKED_CAST")
    override fun create(): GatewaySettings = GatewaySettings(
        gatewayContext,
        compression,
        interceptors,
        guildSubscriptionsStrategy,
        compressionStrategy,
        shardCount,
        threshold,
        request,
        intents,
        gatewayUrl
    )
}