package ru.tesserakt.diskordin.impl.core.cache

import kotlinx.coroutines.flow.Flow
import ru.tesserakt.diskordin.core.entity.IEntity
import ru.tesserakt.diskordin.util.Identified
import kotlin.reflect.KClass

typealias Cache <T> = Flow<Identified<T>>

class ObjectCache<T : IEntity> internal constructor(@Suppress("UNUSED_PARAMETER") type: KClass<T>)

//val genericCacheFun by Diskordin.kodein.factory<KClass<out IEntity>, ObjectCache<out IEntity>>()
//@Suppress("UNCHECKED_CAST")
//inline fun <reified T : IEntity> genericCache() = genericCacheFun(T::class) as ObjectCache<T>