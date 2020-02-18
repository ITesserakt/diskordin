package org.tesserakt.diskordin.gateway.shard

import kotlinx.coroutines.delay
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.gateway.GatewayConnection
import org.tesserakt.diskordin.gateway.json.commands.Identify
import org.tesserakt.diskordin.gateway.sendPayload

class ShardController internal constructor(
    private val context: BootstrapContext.Gateway.Connection.ShardSettings,
    private val connection: List<GatewayConnection>
) {
    private val shards = mutableListOf<Shard>()

    private val connectionProperties = Identify.ConnectionProperties(
        System.getProperty("os.name"),
        "Diskordin",
        "Diskordin"
    )

    private fun isShardCompressed(shardIndex: Int) = when (context.compressionStrategy) {
        CompressionStrategy.CompressAll -> true
        is CompressionStrategy.CompressOnly -> shardIndex in context.compressionStrategy.shardIndexes
    }

    private fun isShardSubscribed(shardIndex: Int) = when (context.guildSubscriptionsStrategy) {
        GuildSubscriptionsStrategy.SubscribeToAll -> true
        is GuildSubscriptionsStrategy.SubscribeTo -> shardIndex in context.guildSubscriptionsStrategy.shardIndexes
    }

    private fun getShardIntents(shardIndex: Int) = when (context.intents) {
        IntentsStrategy.EnableAll -> null
        is IntentsStrategy.EnableOnly -> context.intents.enabled[shardIndex]
    }

    private fun getShardThreshold(shardIndex: Int) = context.shardThresholdOverrides.overrides[shardIndex] ?: 50

    suspend fun openShard(shardIndex: Int, sequence: () -> Int?) {
        delay(5500 * shardIndex.toLong())

        val identify = Identify(
            context.token,
            connectionProperties,
            isShardCompressed(shardIndex),
            getShardThreshold(shardIndex),
            arrayOf(shardIndex, context.shardCount),
            context.initialPresence,
            getShardIntents(shardIndex),
            isShardSubscribed(shardIndex)
        )

        connection[shardIndex].sendPayload(identify, sequence(), shardIndex)
    }

    fun approveShard(shard: Shard, sessionId: String) {
        shard.sessionId = sessionId
        shards += shard
    }
}