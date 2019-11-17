package org.tesserakt.diskordin.rest.storage

import arrow.Kind
import arrow.core.*
import arrow.core.extensions.id.applicative.just
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IEntity

private val cache = mutableMapOf<Snowflake, IEntity>()

val idInterpreter = object : FunctionK<ForCacheF, ForId> {
    @Suppress("UNCHECKED_CAST")
    override fun <A> invoke(fa: Kind<ForCacheF, A>): Id<A> {
        return when (val op = fa.fix()) {
            is CacheF.Put<*> -> {
                cache[op.value.id] = op.value
                Id.just(op.value)
            }
            is CacheF.Get<*> -> Id.just(cache.getOption(op.id))
            is CacheF.Delete<*> -> cache.remove(op.id).toOption().just()
            CacheF.Invalidate -> cache.clear().just()
        } as Id<A>
    }
}