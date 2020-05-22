package org.tesserakt.diskordin.commands.integration

import arrow.core.Eval
import org.tesserakt.diskordin.commands.CommandRegistry
import org.tesserakt.diskordin.commands.resolver.ResolversProvider

data class Commands(
    val resolversProvider: ResolversProvider,
    val registry: Eval<CommandRegistry>
)