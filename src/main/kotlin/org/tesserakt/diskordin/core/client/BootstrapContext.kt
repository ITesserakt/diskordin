package org.tesserakt.diskordin.core.client

import arrow.core.Eval
import okhttp3.OkHttpClient
import org.tesserakt.diskordin.core.data.EntityCache
import org.tesserakt.diskordin.core.data.json.request.UserStatusUpdateRequest
import org.tesserakt.diskordin.gateway.interceptor.Interceptor
import org.tesserakt.diskordin.gateway.shard.CompressionStrategy
import org.tesserakt.diskordin.gateway.shard.GuildSubscriptionsStrategy
import org.tesserakt.diskordin.gateway.shard.IntentsStrategy
import org.tesserakt.diskordin.gateway.shard.ShardThreshold
import org.tesserakt.diskordin.rest.RestClient
import kotlin.coroutines.CoroutineContext

data class BootstrapContext(
    val cache: EntityCache,
    val restClient: Eval<RestClient>,
    val gatewayContext: Gateway
) {
    data class Gateway(
        val scheduler: CoroutineContext,
        val interceptors: List<Interceptor<out Interceptor.Context>>,
        val connectionContext: Connection
    ) {
        data class Connection(
            val httpClient: Eval<OkHttpClient>,
            val url: String,
            val compression: String,
            val shardSettings: ShardSettings
        ) {
            data class ShardSettings(
                val token: String,
                val shardCount: Eval<Int>,
                val compressionStrategy: CompressionStrategy,
                val guildSubscriptionsStrategy: GuildSubscriptionsStrategy,
                val shardThresholdOverrides: ShardThreshold,
                val initialPresence: UserStatusUpdateRequest?,
                val intents: IntentsStrategy
            )
        }
    }
}