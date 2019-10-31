package ru.tesserakt.diskordin.core.data

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForEval
import arrow.core.fix
import arrow.mtl.Kleisli
import ru.tesserakt.diskordin.core.entity.IEntity

@Deprecated("Replacing with arrow type system")
class Identified<T : IEntity>(val id: Snowflake, private val render: suspend (Snowflake) -> T) {
    suspend operator fun invoke() = render(id)
    operator fun component1() = id
    suspend operator fun component2() = this()

    fun update(newId: Snowflake) = Identified(newId, render)
    override fun toString(): String {
        return "Identified(id=$id, render=$render)"
    }
}

infix fun <T : IEntity> Snowflake.combine(render: suspend (Snowflake) -> T) =
    Identified(this) { render(it) }

data class IdentifiedNew<F, E : IEntity>(
    val id: Snowflake,
    private val render: Kleisli<ForEval, Snowflake, Kind<F, E>>
) {
    operator fun invoke() = render.run(id).fix().value()
}

infix fun <F, E : IEntity> Snowflake.combineNew(render: (Snowflake) -> Eval<Kind<F, E>>) =
    IdentifiedNew(this, Kleisli(render))
