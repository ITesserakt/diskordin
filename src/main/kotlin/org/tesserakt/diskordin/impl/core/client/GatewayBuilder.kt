@file:Suppress("unused", "NOTHING_TO_INLINE")

package org.tesserakt.diskordin.impl.core.client

import arrow.fx.typeclasses.Concurrent
import kotlinx.coroutines.Dispatchers
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

@RequestBuilder
class GatewayBuilder<F>(CC: Concurrent<F>) : BuilderBase<GatewayBuilder.GatewaySettings<F>>() {
    data class GatewaySettings<F>(
        val coroutineContext: CoroutineContext,
        val compression: String,
        val interceptors: List<Interceptor<out Interceptor.Context, F>>,
        val guildSubscriptionsStrategy: GuildSubscriptionsStrategy,
        val compressionStrategy: CompressionStrategy,
        val shardCount: Int,
        val threshold: ShardThreshold,
        val initialPresence: UserStatusUpdateRequest?,
        val intents: IntentsStrategy
    ) : JsonRequest()

    private var compression = ""
    private var gatewayContext: CoroutineContext = Dispatchers.IO
    private val interceptors: MutableList<Interceptor<out Interceptor.Context, F>> = mutableListOf(
        WebSocketStateInterceptor(CC),
        HeartbeatInterceptor(CC),
        HeartbeatACKInterceptor(CC),
        HelloChain(CC),
        ShardApprover(CC)
    )
    private var guildSubscriptionsStrategy: GuildSubscriptionsStrategy =
        GuildSubscriptionsStrategy.SubscribeTo(emptyList())
    private var compressionStrategy: CompressionStrategy = CompressionStrategy.CompressOnly(emptyList())
    private var shardCount = 0
    private var threshold = ShardThreshold(emptyMap())
    private var request: UserStatusUpdateRequest? = null
    private var intents: IntentsStrategy = IntentsStrategy.EnableAll

    @Suppress("UNCHECKED_CAST")
    operator fun Interceptor<*, F>.unaryPlus() {
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

    inline fun GatewayBuilder<F>.gatewayInterceptor(value: Interceptor<out Interceptor.Context, F>) = value

    inline fun GatewayBuilder<F>.guildSubscriptions(vararg shardIndexes: Int) = shardIndexes.toList()
    inline fun GatewayBuilder<F>.compressShards(vararg shardIndexes: Int) = shardIndexes
    inline fun GatewayBuilder<F>.useShards(count: Int) = ShardCount(count)
    inline fun GatewayBuilder<F>.thresholdOverrides(vararg overrides: Pair<ShardIndex, LargeThreshold>) =
        overrides.toMap()

    inline fun GatewayBuilder<F>.initialPresence(builder: PresenceBuilder.() -> Unit) =
        PresenceBuilder().apply(builder)

    inline fun GatewayBuilder<F>.featureOverrides(shardIndex: Int, value: ValuedEnum<Intents, Short>) =
        shardIndex to value.code

    inline fun GatewayBuilder<F>.featureOverrides(shardIndex: Int, value: IValued<Intents, Short>) =
        shardIndex to value.code

    inline fun GatewayBuilder<F>.coroutineContext(context: CoroutineContext) = context

    @Suppress("UNCHECKED_CAST")
    override fun create(): GatewaySettings<F> = GatewaySettings(
        gatewayContext,
        compression,
        interceptors,
        guildSubscriptionsStrategy,
        compressionStrategy,
        shardCount,
        threshold,
        request,
        intents
    )
}