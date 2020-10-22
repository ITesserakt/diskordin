package org.tesserakt.diskordin.commands.resolver

import arrow.core.right
import org.tesserakt.diskordin.commands.CommandContext

class StringResolver : TypeResolver<String, CommandContext> {
    override suspend fun parse(context: CommandContext, input: String) = input.right()
}