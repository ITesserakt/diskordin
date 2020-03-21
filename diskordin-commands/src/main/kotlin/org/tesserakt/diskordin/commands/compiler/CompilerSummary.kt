package org.tesserakt.diskordin.commands.compiler

import arrow.core.Either
import arrow.core.Nel
import arrow.core.right
import org.tesserakt.diskordin.commands.CommandRegistry
import org.tesserakt.diskordin.commands.ValidationError

data class CompilerSummary(
    val loadedModules: Int = 0,
    val compiledCommands: Int = 0,
    val result: Either<Nel<ValidationError>, CommandRegistry> = CommandRegistry.EMPTY.right()
)