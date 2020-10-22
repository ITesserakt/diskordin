package org.tesserakt.diskordin.commands.resolver

import arrow.core.left
import arrow.core.right
import org.tesserakt.diskordin.commands.CommandContext

class BooleanResolver : TypeResolver<Boolean, CommandContext> {
    data class BooleanConversionError(val input: String) : ConversionError(input, "Boolean")

    override suspend fun parse(context: CommandContext, input: String) =
        when (input.toLowerCase()) {
            "true" -> true.right()
            "false" -> false.right()
            else -> BooleanConversionError(input).left()
        }
}