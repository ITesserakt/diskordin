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
) {
    fun errorsToString(err: Nel<ValidationError>) = """
        |Errors:
        |${err.toList()
        .map { it::class.simpleName to it.description }
        .joinToString("\n") { (className, desc) -> "       $className - $desc" }}
    """.trimMargin()

    override fun toString(): String = """
        |
        |+------------------------------------------------------------------------------------------------------------+
        |Compilation completed ${result.fold({ "with ${it.size} errors" }, { "successfully" })}
        |   Total loaded modules: $loadedModules
        |   Compiled commands:    $compiledCommands
        |   ${result.fold(::errorsToString, CommandRegistry::toString)}
        |+------------------------------------------------------------------------------------------------------------+
    """.trimMargin()
}