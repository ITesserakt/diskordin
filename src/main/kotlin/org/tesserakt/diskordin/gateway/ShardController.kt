package org.tesserakt.diskordin.gateway

import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.gateway.json.commands.Identify
import org.tesserakt.diskordin.gateway.json.commands.Resume

class ShardController internal constructor(
    private val context: BootstrapContext.Gateway.Connection.ShardSettings,
    private val implementation: Implementation
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

    suspend fun openShard(shardIndex: Int) {
        require(shardIndex < context.shardCount) { "Given index of shard less than count" }

        val identify = Identify(
            context.token,
            connectionProperties,
            isShardCompressed(shardIndex),
            context.shardThresholdOverrides.overrides[shardIndex] ?: 50,
            arrayOf(shardIndex, context.shardCount),
            isShardSubscribed(shardIndex)
        )

        implementation.sendPayload(identify, sequenceId)
    }

    internal fun approveShard(shardIndex: Int, sessionId: String) {
        shards += Shard(
            context.token, sessionId, shardIndex to context.shardCount
        )
    }

    internal suspend fun resumeShard(shardIndex: Int) {
        val shard = shards.first { it.shardData.first == shardIndex }
        val resume = Resume(context.token, shard.sessionId, sequenceId)

        implementation.sendPayload(resume, sequenceId)
    }
}