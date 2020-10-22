package org.tesserakt.diskordin.commands.resolver

import arrow.core.left
import arrow.core.right
import org.tesserakt.diskordin.commands.CommandContext

class CharResolver : TypeResolver<Char, CommandContext> {
    data class LengthError(val length: Int) : ParseError("Expected one character, given $length")

    override suspend fun parse(context: CommandContext, input: String) =
        if (input.length != 1) LengthError(input.length).left()
        else input[0].right()
}