package org.tesserakt.diskordin.commands.resolver

import arrow.core.left
import arrow.core.right
import org.tesserakt.diskordin.commands.CommandContext
import java.util.*

class BooleanResolver : TypeResolver<Boolean, CommandContext> {
    data class BooleanConversionError(val input: String) : ConversionError(input, "Boolean")

    override suspend fun parse(context: CommandContext, input: String) =
        when (input.lowercase(Locale.getDefault())) {
            "true" -> true.right()
            "false" -> false.right()
            else -> BooleanConversionError(input).left()
        }
}