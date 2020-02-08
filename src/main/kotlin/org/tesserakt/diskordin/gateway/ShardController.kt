package org.tesserakt.diskordin.gateway

import org.tesserakt.diskordin.gateway.json.commands.Identify
import org.tesserakt.diskordin.gateway.json.commands.Resume

class ShardController internal constructor(
    private val shardCount: Int,
    private val implementation: Implementation,
    private val token: String,
    private val compress: CompressionStrategy,
    private val guildSubscriptions: GuildSubscriptionsStrategy
) {
    private val shards = mutableListOf<Shard>()

    private val connectionProperties = Identify.ConnectionProperties(
        System.getProperty("os.name"),
        "Diskordin",
        "Diskordin"
    )

    private fun isShardCompressed(shardIndex: Int) = when (compress) {
        CompressionStrategy.CompressAll -> true
        is CompressionStrategy.CompressOnly -> shardIndex in compress.shardIndexes
    }

    private fun isShardSubscribed(shardIndex: Int) = when (guildSubscriptions) {
        GuildSubscriptionsStrategy.SubscribeToAll -> true
        is GuildSubscriptionsStrategy.SubscribeTo -> shardIndex in guildSubscriptions.shardIndexes
    }

    suspend fun openShard(shardIndex: Int) {
        require(shardIndex < shardCount) { "Given index of shard less than count" }

        val identify = Identify(
            token,
            connectionProperties,
            isShardCompressed(shardIndex),
            50, //TODO: Add threshold settings
            arrayOf(shardIndex, shardCount),
            isShardSubscribed(shardIndex)
        )

        implementation.sendPayload(identify, sequenceId)
    }

    internal fun approveShard(shardIndex: Int, sessionId: String) {
        shards += Shard(
            token, sessionId, shardIndex to shardCount
        )
    }

    internal suspend fun resumeShard(shardIndex: Int) {
        val shard = shards.first { it.shardData.first == shardIndex }
        val resume = Resume(token, shard.sessionId, sequenceId)

        implementation.sendPayload(resume, sequenceId)
    }
}