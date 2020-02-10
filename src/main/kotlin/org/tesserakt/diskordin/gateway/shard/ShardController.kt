package org.tesserakt.diskordin.gateway.shard

import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.gateway.Implementation
import org.tesserakt.diskordin.gateway.json.commands.Identify
import org.tesserakt.diskordin.gateway.json.commands.Resume
import org.tesserakt.diskordin.gateway.sendPayload
import org.tesserakt.diskordin.gateway.sequenceId

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

    private fun getShardIntents(shardIndex: Int) = when (context.intents) {
        IntentsStrategy.EnableAll -> Short.MAX_VALUE
        is IntentsStrategy.EnableOnly -> context.intents.enabled[shardIndex] ?: Short.MAX_VALUE
    }

    suspend fun openShard(shardIndex: Int) {
        require(shardIndex < context.shardCount) { "Given index of shard less than count" }

        val needUpdatedPresence = if (shardIndex == 0)
            context.initialPresence
        else null

        val identify = Identify(
            context.token,
            connectionProperties,
            isShardCompressed(shardIndex),
            context.shardThresholdOverrides.overrides[shardIndex] ?: 50,
            arrayOf(shardIndex, context.shardCount),
            needUpdatedPresence,
            getShardIntents(shardIndex),
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
        val resume = Resume(
            context.token, shard.sessionId,
            sequenceId
        )

        implementation.sendPayload(resume, sequenceId)
    }
}