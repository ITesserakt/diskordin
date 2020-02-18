package org.tesserakt.diskordin.gateway.interceptor

import org.tesserakt.diskordin.gateway.shard.Shard
import org.tesserakt.diskordin.gateway.shard.ShardController
import kotlin.reflect.KClass

interface Interceptor<T : Interceptor.Context> {
    abstract class Context(
        val controller: ShardController,
        val shard: Shard
    )

    val selfContext: KClass<T>

    suspend fun intercept(context: T)
}