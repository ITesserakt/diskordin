package org.tesserakt.diskordin.core.client

import arrow.Kind
import arrow.fx.typeclasses.Concurrent
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

data class BootstrapContext<F, G>(
    val cache: MutableMap<Snowflake, IEntity>,
    val restClient: RestClient<F>,
    val gatewayContext: Gateway<G>
) {
    data class Gateway<F>(
        val scheduler: CoroutineContext,
        val interceptors: List<Interceptor<out Interceptor.Context, F>>,
        val CC: Concurrent<F>,
        val runner: suspend (Kind<F, *>) -> Unit,
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