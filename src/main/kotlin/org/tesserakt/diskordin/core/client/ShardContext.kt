package org.tesserakt.diskordin.core.client

import arrow.core.Eval
import org.tesserakt.diskordin.core.data.json.request.UserStatusUpdateRequest
import org.tesserakt.diskordin.gateway.shard.CompressionStrategy
import org.tesserakt.diskordin.gateway.shard.GuildSubscriptionsStrategy
import org.tesserakt.diskordin.gateway.shard.IntentsStrategy
import org.tesserakt.diskordin.gateway.shard.ShardThreshold

data class ShardContext(
    val token: String,
    val shardCount: Eval<Int>,
    val compressionStrategy: CompressionStrategy,
    val guildSubscriptionsStrategy: GuildSubscriptionsStrategy,
    val shardThresholdOverrides: ShardThreshold,
    val initialPresence: UserStatusUpdateRequest?,
    val intents: IntentsStrategy
) : BootstrapContext.ExtensionContext {
    companion object : BootstrapContext.PersistentExtension<ShardContext>
}