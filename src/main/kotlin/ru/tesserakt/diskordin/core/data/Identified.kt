package ru.tesserakt.diskordin.core.data

import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.fix
import arrow.fx.typeclasses.ConcurrentSyntax
import arrow.mtl.Kleisli
import ru.tesserakt.diskordin.core.entity.IEntity

data class Identified<E : IEntity>(
    val id: Snowflake,
    private val render: Kleisli<ForIO, Snowflake, E>
) {
    operator fun invoke() = render.run(id).fix()
}

infix fun <E : IEntity> Snowflake.identify(render: suspend ConcurrentSyntax<ForIO>.(Snowflake) -> E) =
    Identified<E>(this, Kleisli {
        IO.fx { render(it) }
    })

