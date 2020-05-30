package org.tesserakt.diskordin.commands.resolver

import arrow.mtl.EitherT
import arrow.typeclasses.Applicative
import org.tesserakt.diskordin.commands.CommandContext

class BooleanResolver<F>(private val AP: Applicative<F>) : TypeResolver<Boolean, F, CommandContext<F>> {
    data class BooleanConversionError(val input: String) : ConversionError(input, "Boolean")

    override fun parse(context: CommandContext<F>, input: String): EitherT<out ParseError, F, Boolean> =
        when (input.toLowerCase()) {
            "true" -> EitherT.just(AP, true)
            "false" -> EitherT.just(AP, false)
            else -> EitherT.left(AP, BooleanConversionError(input))
        }
}