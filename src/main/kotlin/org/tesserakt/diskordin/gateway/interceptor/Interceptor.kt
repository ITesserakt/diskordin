package org.tesserakt.diskordin.gateway.interceptor

import org.tesserakt.diskordin.gateway.GatewayConnection
import org.tesserakt.diskordin.gateway.json.commands.GatewayCommand
import org.tesserakt.diskordin.gateway.sendPayload
import org.tesserakt.diskordin.gateway.shard.ShardController
import kotlin.reflect.KClass

interface Interceptor<T : Interceptor.Context> {
    abstract class Context(
        internal val connection: GatewayConnection,
        val controller: ShardController,
        internal val sequenceId: () -> Int?,
        val shardIndex: Int
    )

    val selfContext: KClass<T>

    suspend fun intercept(context: T)

    suspend fun Context.sendPayload(data: GatewayCommand) =
        connection.sendPayload(data, sequenceId(), shardIndex)
}