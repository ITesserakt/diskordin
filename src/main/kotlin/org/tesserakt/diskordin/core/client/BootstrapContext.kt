package org.tesserakt.diskordin.core.client

import arrow.core.Eval
import org.tesserakt.diskordin.core.data.json.request.UserStatusUpdateRequest
import org.tesserakt.diskordin.gateway.interceptor.Interceptor
import org.tesserakt.diskordin.gateway.shard.CompressionStrategy
import org.tesserakt.diskordin.gateway.shard.GuildSubscriptionsStrategy
import org.tesserakt.diskordin.gateway.shard.IntentsStrategy
import org.tesserakt.diskordin.gateway.shard.ShardThreshold
import kotlin.coroutines.CoroutineContext

@Suppress("UNCHECKED_CAST")
class BootstrapContext(
    extra: Map<Extension<*>, ExtensionContext>
) {
    private val extensions: MutableMap<Extension<*>, ExtensionContext> = extra.toMutableMap()

    operator fun <E : Extension<C>, C : ExtensionContext> get(ext: E) = extensions[ext] as? C
    operator fun <P : PersistentExtension<C>, C : ExtensionContext> get(ext: P) = extensions[ext] as C

    operator fun <C : ExtensionContext, E : Extension<C>> set(ext: E, ctx: C) {
        extensions[ext] = ctx
    }

    companion object

    interface ExtensionContext
    interface Extension<C : ExtensionContext>
    interface PersistentExtension<C : ExtensionContext> : Extension<C>
}

data class GatewayContext(
    val scheduler: CoroutineContext,
    val interceptors: List<Interceptor<out Interceptor.Context>>
) : BootstrapContext.ExtensionContext {
    companion object : BootstrapContext.PersistentExtension<GatewayContext>
}

data class ConnectionContext(
    val url: String,
    val compression: String
) : BootstrapContext.ExtensionContext {
    companion object : BootstrapContext.PersistentExtension<ConnectionContext>
}

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