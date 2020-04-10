package org.tesserakt.diskordin.commands.resolver

import arrow.mtl.EitherT
import arrow.typeclasses.Applicative

class CharResolver<F>(private val AP: Applicative<F>) : TypeResolver<Char, F, Nothing> {
    data class LengthError(val length: Int) : ParseError("Expected one character, given $length")

    override fun parse(context: Nothing, input: String): EitherT<out ParseError, F, Char> =
        if (input.length != 1) EitherT.left(AP, LengthError(input.length))
        else EitherT.right(AP, input[0])
}