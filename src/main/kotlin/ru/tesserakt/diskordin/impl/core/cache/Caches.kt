package ru.tesserakt.diskordin.impl.core.cache

import kotlinx.coroutines.Deferred
import org.kodein.di.generic.factory
import ru.tesserakt.diskordin.Diskordin
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.IEntity
import ru.tesserakt.diskordin.util.Identified
import kotlin.reflect.KClass

typealias Cache <T> = MutableList<Identified<T>>

sealed class CacheBase<T>(cache: Cache<out T> = mutableListOf()) :
    MutableMap<Snowflake, Deferred<T>> by cache.associateBy(
        { it.state },
        { it.extract() }
    ).toMutableMap()

class ObjectCache<T : IEntity> internal constructor(@Suppress("UNUSED_PARAMETER") type: KClass<T>) : CacheBase<T>()

private val genericCacheFun by Diskordin.kodein.factory<KClass<out IEntity>, ObjectCache<out IEntity>>()
@Suppress("UNCHECKED_CAST")
val <T : IEntity> KClass<T>.genericCache: ObjectCache<T>
    get() = genericCacheFun(this) as ObjectCache<T>