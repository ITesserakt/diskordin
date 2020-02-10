package org.tesserakt.diskordin.core.client

import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.UserStatusUpdateRequest
import org.tesserakt.diskordin.core.entity.IEntity
import org.tesserakt.diskordin.gateway.interceptor.Interceptor
import org.tesserakt.diskordin.gateway.shard.CompressionStrategy
import org.tesserakt.diskordin.gateway.shard.GuildSubscriptionsStrategy
import org.tesserakt.diskordin.gateway.shard.IntentsStrategy
import org.tesserakt.diskordin.gateway.shard.ShardThreshold
import org.tesserakt.diskordin.rest.RestClient
import kotlin.coroutines.CoroutineContext

data class BootstrapContext<F>(
    val cache: MutableMap<Snowflake, IEntity>,
    val restClient: RestClient<F>,
    val gatewayContext: Gateway
) {
    data class Gateway(
        val scheduler: CoroutineContext,
        val lifecycleRegistry: GatewayLifecycleManager,
        val interceptors: Flow<Interceptor<Interceptor.Context>>,
        val connectionContext: Connection
    ) {
        data class Connection(
            val httpClient: OkHttpClient,
            val url: String,
            val compression: String,
            val shardSettings: ShardSettings
        ) {
            data class ShardSettings(
                val token: String,
                val shardCount: Int,
                val compressionStrategy: CompressionStrategy,
                val guildSubscriptionsStrategy: GuildSubscriptionsStrategy,
                val shardThresholdOverrides: ShardThreshold,
                val initialPresence: UserStatusUpdateRequest?,
                val intents: IntentsStrategy
            )
        }
    }
}