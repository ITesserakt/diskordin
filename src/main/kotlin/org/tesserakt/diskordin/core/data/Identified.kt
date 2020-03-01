package org.tesserakt.diskordin.core.data

import arrow.Kind
import arrow.core.ForId
import arrow.core.andThen
import arrow.typeclasses.Comonad
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import org.tesserakt.diskordin.core.entity.IEntity

data class IdentifiedF<F, E : IEntity>(
    val id: Snowflake,
    private val render: (Snowflake) -> Kind<F, E>
) {
    operator fun invoke() = render(id)
    operator fun invoke(CM: Comonad<F>) = CM.run { invoke().extract() }

    fun <B : IEntity> map(FN: Functor<F>, f: (E) -> B) = FN.run {
        id identify (render andThen { it.map(f) })
    }

    fun <B : IEntity> flatMap(M: Monad<F>, f: (E) -> IdentifiedF<F, B>) = M.run {
        id identify (render andThen { render -> render.flatMap { f(it).render(id) } })
    }

    override fun toString(): String {
        return "{$id -> thunk()}"
    }
}

typealias Identified<E> = IdentifiedF<ForId, E>

infix fun <F, E : IEntity> Snowflake.identify(render: (Snowflake) -> Kind<F, E>) =
    IdentifiedF(this, render)