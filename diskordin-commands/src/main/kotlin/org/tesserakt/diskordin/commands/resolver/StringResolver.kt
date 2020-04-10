package org.tesserakt.diskordin.commands.resolver

import arrow.mtl.EitherT
import arrow.typeclasses.Applicative

class StringResolver<F>(private val AP: Applicative<F>) : TypeResolver<String, F, Nothing> {
    override fun parse(context: Nothing, input: String): EitherT<out ParseError, F, String> =
        EitherT.just(AP, input)
}