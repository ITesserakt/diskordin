package org.tesserakt.diskordin.gateway.shard

import mu.KotlinLogging
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.core.client.GatewayLifecycleManager
import org.tesserakt.diskordin.gateway.GatewayConnection
import org.tesserakt.diskordin.gateway.json.commands.Identify
import org.tesserakt.diskordin.gateway.json.commands.Resume
import org.tesserakt.diskordin.gateway.sendPayload

private const val INDEX_OUT_OF_SHARD_COUNT = "Given index of shard more than shard count"

class ShardController internal constructor(
    private val context: BootstrapContext.Gateway.Connection.ShardSettings,
    private val connection: List<GatewayConnection>,
    private val lifecycles: List<GatewayLifecycleManager>
) {
    private val logger = KotlinLogging.logger { }
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
        require(shardIndex < context.shardCount) { INDEX_OUT_OF_SHARD_COUNT }
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

    fun closeShard(shardIndex: Int) {
        require(shardIndex < context.shardCount) { INDEX_OUT_OF_SHARD_COUNT }

        logger.info("Closing shard #$shardIndex")
        lifecycles[shardIndex].stop()
        shards.first { it.shardData.current == shardIndex }.let {
            it.state = Shard.State.Disconnected
            shards.remove(it)
        }
    }

    suspend fun resumeShard(shard: Shard) {
        val resume = Resume(shard.token, shard.sessionId, shard.sequence)
        connection[shard.shardData.current].sendPayload(resume, shard.sequence, shard.shardData.current)
    }
}