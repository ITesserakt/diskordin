package org.tesserakt.diskordin.commands.resolver

import arrow.mtl.EitherT
import arrow.typeclasses.Applicative
import org.tesserakt.diskordin.commands.CommandContext

class StringResolver<F>(private val AP: Applicative<F>) : TypeResolver<String, F, CommandContext<F>> {
    override fun parse(context: CommandContext<F>, input: String): EitherT<out ParseError, F, String> =
        EitherT.just(AP, input)
}