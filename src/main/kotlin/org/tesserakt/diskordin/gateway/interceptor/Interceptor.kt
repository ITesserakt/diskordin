package org.tesserakt.diskordin.gateway.interceptor

import arrow.Kind
import org.tesserakt.diskordin.gateway.shard.Shard
import org.tesserakt.diskordin.gateway.shard.ShardController
import kotlin.reflect.KClass

interface Interceptor<C : Interceptor.Context, F> {
    abstract class Context(
        val controller: ShardController,
        val shard: Shard
    )

    val selfContext: KClass<C>

    fun intercept(context: C): Kind<F, Unit>
}