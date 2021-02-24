package org.tesserakt.diskordin.core.data

import arrow.syntax.function.andThen
import arrow.syntax.function.bind
import org.tesserakt.diskordin.core.entity.IEntity
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface IIdentified<out E : IEntity>

class EagerIdentified<out E : IEntity>(val entity: E) : IIdentified<E>, ReadOnlyProperty<Nothing?, E> {
    val id get() = entity.id
    operator fun invoke() = entity

    override fun getValue(thisRef: Nothing?, property: KProperty<*>): E = invoke()
}

data class LazyIdentified<out E : IEntity>(val entity: () -> E) : IIdentified<E>, ReadOnlyProperty<Nothing?, E> {
    val id by lazy { entity().id }
    operator fun invoke() = entity()

    fun <B> map(f: (E) -> B) = lazy(entity andThen f)

    override fun getValue(thisRef: Nothing?, property: KProperty<*>): E = invoke()
}

data class DeferredIdentified<out E : IEntity>(val id: Snowflake, private val render: suspend (Snowflake) -> E) :
    IIdentified<E> {
    suspend operator fun invoke() = render(id)

    suspend fun getValue(): E = invoke()
}

inline infix fun <E : IEntity> Snowflake.eager(render: (Snowflake) -> E) = render(this).identified()
infix fun <E : IEntity> Snowflake.deferred(render: suspend (Snowflake) -> E) = DeferredIdentified(this, render)
infix fun <E : IEntity> Snowflake.lazy(render: (Snowflake) -> E) = LazyIdentified(render.bind(this))
fun <E : IEntity> E.identified() = EagerIdentified(this)