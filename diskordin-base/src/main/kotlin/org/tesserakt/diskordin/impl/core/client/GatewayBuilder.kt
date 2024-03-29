@file:Suppress("unused", "NOTHING_TO_INLINE")

package org.tesserakt.diskordin.impl.core.client

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.tesserakt.diskordin.core.client.InternalTestAPI
import org.tesserakt.diskordin.core.data.json.request.JsonRequest
import org.tesserakt.diskordin.core.data.json.request.UserStatusUpdateRequest
import org.tesserakt.diskordin.core.entity.builder.BuilderBase
import org.tesserakt.diskordin.core.entity.builder.PresenceBuilder
import org.tesserakt.diskordin.core.entity.builder.RequestBuilder
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor
import org.tesserakt.diskordin.gateway.interceptor.Interceptor
import org.tesserakt.diskordin.gateway.interceptor.TokenInterceptor
import org.tesserakt.diskordin.gateway.shard.*
import org.tesserakt.diskordin.impl.gateway.interceptor.*
import org.tesserakt.diskordin.util.enums.IValued
import kotlin.coroutines.CoroutineContext

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
        val intents: Short,
        val url: String
    ) : JsonRequest()

    private var gatewayUrl = "wss://gateway.discord.gg"
    private var compression = ""
    private var gatewayContext: CoroutineContext = Dispatchers.IO
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
    private var intents: IValued<Intents, Short> = Intents.allNonPrivileged

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

    operator fun IValued<Intents, Short>.unaryPlus() {
        intents = this
    }

    operator fun CoroutineContext.unaryPlus() {
        gatewayContext = this
    }

    operator fun String.unaryPlus() {
        gatewayUrl = this
    }

    inline fun GatewayBuilder.gatewayInterceptor(value: Interceptor<out Interceptor.Context>) = value
    inline fun GatewayBuilder.gatewayInterceptor(crossinline block: suspend (TokenInterceptor.Context) -> Unit) =
        object : TokenInterceptor() {
            override val scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined + Job() + exceptionHandler)

            override suspend fun intercept(context: Context) = block(context)
        }

    inline fun GatewayBuilder.gatewayInterceptor(crossinline block: suspend (EventInterceptor.Context) -> Unit) =
        object : EventInterceptor() {
            override val scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined + Job() + exceptionHandler)

            override suspend fun intercept(context: Context) = block(context)
        }

    @Deprecated("Replaced with Intents", ReplaceWith("featureOverrides()"))
    inline fun GatewayBuilder.guildSubscriptions(vararg shardIndexes: Int) = shardIndexes.toList()
    inline fun GatewayBuilder.compressShards(vararg shardIndexes: Int) = shardIndexes
    inline fun GatewayBuilder.useShards(count: Int) = ShardCount(count)
    inline fun GatewayBuilder.thresholdOverrides(vararg overrides: Pair<ShardIndex, LargeThreshold>) =
        overrides.toMap()

    inline fun GatewayBuilder.initialPresence(builder: PresenceBuilder.() -> Unit) =
        PresenceBuilder().apply(builder)

    inline fun GatewayBuilder.featureOverrides(value: IValued<Intents, Short>) = value

    inline fun GatewayBuilder.coroutineContext(context: CoroutineContext) = context

    @InternalTestAPI
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
        intents.code,
        gatewayUrl
    )
}