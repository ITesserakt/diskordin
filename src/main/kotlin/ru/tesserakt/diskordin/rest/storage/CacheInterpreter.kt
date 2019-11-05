package ru.tesserakt.diskordin.rest.storage

import arrow.Kind
import arrow.core.*
import arrow.core.extensions.id.applicative.just
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.IEntity

private val cache = mutableMapOf<Snowflake, IEntity>()

val idInterpreter = object : FunctionK<ForCache, ForId> {
    @Suppress("UNCHECKED_CAST")
    override fun <A> invoke(fa: Kind<ForCache, A>): Id<A> {
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