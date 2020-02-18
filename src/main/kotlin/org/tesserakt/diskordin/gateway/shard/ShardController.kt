package org.tesserakt.diskordin.gateway.shard

import kotlinx.coroutines.delay
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.gateway.Implementation
import org.tesserakt.diskordin.gateway.json.commands.Identify
import org.tesserakt.diskordin.gateway.json.commands.Resume
import org.tesserakt.diskordin.gateway.sendPayload
import org.tesserakt.diskordin.gateway.sequenceId
import java.util.concurrent.atomic.AtomicBoolean

class ShardController internal constructor(
    private val context: BootstrapContext.Gateway.Connection.ShardSettings,
    private val implementation: List<Implementation>
) {
    private val anotherShardOpens = AtomicBoolean(false)
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

    suspend fun openShard(shardIndex: Int) {
        delay(5500 * shardIndex.toLong())

        val needUpdatedPresence = if (shardIndex == 0)
            context.initialPresence
        else null

        val identify = Identify(
            context.token,
            connectionProperties,
            isShardCompressed(shardIndex),
            getShardThreshold(shardIndex),
            arrayOf(shardIndex, context.shardCount),
            needUpdatedPresence,
            getShardIntents(shardIndex),
            isShardSubscribed(shardIndex)
        )

        implementation[shardIndex].sendPayload(identify, sequenceId, shardIndex)
    }

    internal fun approveShard(shardIndex: Int, sessionId: String) {
        shards += Shard(
            context.token, sessionId, shardIndex to context.shardCount
        )
        anotherShardOpens.set(false)
    }

    internal suspend fun resumeShard(shardIndex: Int) {
        val shard = shards.first { it.shardData.first == shardIndex }
        val resume = Resume(
            context.token, shard.sessionId,
            sequenceId
        )

        implementation[shardIndex].sendPayload(resume, sequenceId, shardIndex)
    }

    override fun toString(): String {
        return "ShardController { Opened: ${shards.size}, total: ${context.shardCount} })"
    }
}