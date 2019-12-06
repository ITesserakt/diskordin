package org.tesserakt.diskordin.core.data

import arrow.Kind
import arrow.fx.ForIO
import arrow.mtl.Kleisli
import org.tesserakt.diskordin.core.entity.IEntity

data class IdentifiedF<F, E : IEntity>(
    val id: Snowflake,
    private val render: Kleisli<F, Snowflake, E>
) {
    operator fun invoke() = render.run(id)
}

typealias Identified<E> = IdentifiedF<ForIO, E>

infix fun <F, E : IEntity> Snowflake.identify(render: (Snowflake) -> Kind<F, E>) =
    IdentifiedF(this, Kleisli(render))

