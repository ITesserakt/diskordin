package org.tesserakt.diskordin.commands.resolver

import arrow.mtl.EitherT
import arrow.typeclasses.Applicative

class BooleanResolver<F>(private val AP: Applicative<F>) : TypeResolver<Boolean, F, Nothing> {
    override fun parse(context: Nothing, input: String): EitherT<out ParseError, F, Boolean> =
        EitherT.just(AP, input.toBoolean())
}