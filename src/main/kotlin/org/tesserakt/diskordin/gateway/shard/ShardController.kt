package org.tesserakt.diskordin.gateway.shard

import arrow.fx.coroutines.ForkConnected
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import mu.KotlinLogging
import org.tesserakt.diskordin.core.client.ShardContext
import org.tesserakt.diskordin.gateway.GatewayLifecycleManager
import org.tesserakt.diskordin.gateway.json.commands.Identify
import org.tesserakt.diskordin.gateway.json.commands.Resume
import org.tesserakt.diskordin.gateway.sendPayload
import org.tesserakt.diskordin.impl.gateway.interceptor.ConnectionObserver

private const val INDEX_OUT_OF_SHARD_COUNT = "Given index of shard more than shard count"

class ShardController internal constructor(
    private val context: ShardContext,
    private val lifecycles: List<GatewayLifecycleManager>
) {
    private val logger = KotlinLogging.logger { }

    @ExperimentalCoroutinesApi
    private val shards = mutableListOf<Shard>()
    private val observer = ConnectionObserver(this)

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

    @ExperimentalCoroutinesApi
    suspend fun openShard(shardIndex: Int, sequence: StateFlow<Int?>) {
        require(shardIndex < context.shardCount.extract()) { INDEX_OUT_OF_SHARD_COUNT }
        val identify = Identify(
            context.token,
            connectionProperties,
            isShardCompressed(shardIndex),
            getShardThreshold(shardIndex),
            listOf(shardIndex, context.shardCount.extract()),
            context.initialPresence,
            getShardIntents(shardIndex),
            isShardSubscribed(shardIndex)
        )

        lifecycles[shardIndex].connection.sendPayload(identify, sequence.value, shardIndex)
    }

    @ExperimentalCoroutinesApi
    suspend fun approveShard(shard: Shard, sessionId: String) {
        shard._sessionId.value = sessionId
        shards += shard
        ForkConnected { observer.observe(shard) }
    }

    @ExperimentalCoroutinesApi
    suspend fun closeShard(shardIndex: Int) {
        require(shardIndex < context.shardCount.extract()) { INDEX_OUT_OF_SHARD_COUNT }
        val shard = shards.find { it.shardData.index == shardIndex }

        if (shard == null || shard.state.value == Shard.State.Closing) return
        shard._state.value = Shard.State.Closing

        logger.info("Closing shard #$shardIndex")
        lifecycles[shardIndex].stop(1000, "Normal closing")
        shard.let {
            it._state.value = Shard.State.Disconnected
            shards.remove(it)
        }
    }

    @ExperimentalCoroutinesApi
    suspend fun resumeShard(shard: Shard) {
        lifecycles[shard.shardData.index].start()
        val resume = Resume(shard.token, shard.sessionId.value!!, shard.sequence.value)
        lifecycles[shard.shardData.index].connection.sendPayload(resume, shard.sequence.value, shard.shardData.index)
    }
}