package org.tesserakt.diskordin.gateway.interceptor

import org.tesserakt.diskordin.gateway.shard.Shard
import org.tesserakt.diskordin.gateway.shard.ShardController
import kotlin.reflect.KClass

interface Interceptor<C : Interceptor.Context> {
    abstract class Context constructor(
        val controller: ShardController,
        val shard: Shard
    )

    val name get() = "Interceptor#${hashCode()}"
    val selfContext: KClass<C>

    suspend fun intercept(context: C)
}