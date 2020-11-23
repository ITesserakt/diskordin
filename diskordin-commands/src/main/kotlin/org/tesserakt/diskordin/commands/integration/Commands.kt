package org.tesserakt.diskordin.commands.integration

import arrow.core.Eval
import org.tesserakt.diskordin.commands.CommandRegistry
import org.tesserakt.diskordin.commands.resolver.ResolversProvider
import org.tesserakt.diskordin.core.client.BootstrapContext

data class Commands(
    val resolversProvider: ResolversProvider,
    val registry: Eval<CommandRegistry>
) : BootstrapContext.ExtensionContext {
    companion object : BootstrapContext.Extension<Commands>
}