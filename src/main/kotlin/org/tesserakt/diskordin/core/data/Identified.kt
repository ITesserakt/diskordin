package org.tesserakt.diskordin.core.data

import arrow.Kind
import arrow.core.ForId
import arrow.mtl.Kleisli
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import org.tesserakt.diskordin.core.entity.IEntity

data class IdentifiedF<F, E : IEntity>(
    val id: Snowflake,
    private val render: Kleisli<F, Snowflake, E>
) {
    operator fun invoke() = render.run(id)

    fun <B : IEntity> map(FN: Functor<F>, f: (E) -> B) =
        id identify render.map(FN, f).run

    fun <B : IEntity> flatMap(M: Monad<F>, f: (E) -> IdentifiedF<F, B>) = id identify render.flatMap(M) {
        f(it).render
    }.run
}

typealias Identified<E> = IdentifiedF<ForId, E>

infix fun <F, E : IEntity> Snowflake.identify(render: (Snowflake) -> Kind<F, E>) =
    IdentifiedF(this, Kleisli(render))

