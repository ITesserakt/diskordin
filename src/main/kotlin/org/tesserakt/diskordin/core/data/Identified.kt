package org.tesserakt.diskordin.core.data

import arrow.Kind
import arrow.mtl.Kleisli
import org.tesserakt.diskordin.core.entity.IEntity

data class IdentifiedF<F, E : IEntity>(
    val id: Snowflake,
    private val render: Kleisli<F, Snowflake, E>
) {
    operator fun invoke() = render.run(id)
}

infix fun <F, E : IEntity> Snowflake.identify(render: (Snowflake) -> Kind<F, E>) =
    IdentifiedF(this, Kleisli(render))

