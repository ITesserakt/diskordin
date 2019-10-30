package ru.tesserakt.diskordin.core.data

import arrow.Kind
import arrow.core.Eval
import arrow.mtl.Kleisli
import ru.tesserakt.diskordin.core.entity.IEntity

@Deprecated("Replacing with arrow type system")
class Identified<T : IEntity>(val id: Snowflake, private val render: suspend (Snowflake) -> T) {
    suspend operator fun invoke() = render(id)
    operator fun component1() = id
    suspend operator fun component2() = this()

    fun update(newId: Snowflake) = Identified(newId, render)
}

infix fun <T : IEntity> Snowflake.combine(render: suspend (Snowflake) -> T) =
    Identified(this) { render(it) }

data class IdentifiedNew<F, E : IEntity>(val id: Snowflake, private val render: Eval<Kleisli<F, Snowflake, E>>) {
    operator fun invoke() = render.map { it.run(id) }.value()
}

infix fun <F, E : IEntity> Snowflake.combine(render: Eval<(Snowflake) -> Kind<F, E>>) =
    IdentifiedNew(this, render.map { Kleisli(it) })
