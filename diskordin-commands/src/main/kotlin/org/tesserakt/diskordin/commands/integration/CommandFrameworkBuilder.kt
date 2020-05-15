@file:Suppress("NOTHING_TO_INLINE", "unused")

package org.tesserakt.diskordin.commands.integration

import org.tesserakt.diskordin.commands.compiler.CompilerExtension
import org.tesserakt.diskordin.commands.feature.Feature
import org.tesserakt.diskordin.commands.integration.CompilerOutput.*

class CommandFrameworkBuilder {
    private val extensions = mutableListOf<CompilerExtension<out Feature<*>>>()
    private var outputLogger: Logger = SummaryLogger()
    private var eager = false

    operator fun CompilerExtension<*>.unaryPlus() {
        extensions += this
    }

    operator fun CompilerOutput.unaryPlus() {
        outputLogger = when (this) {
            Verbose -> VerboseLogger()
            Summary -> SummaryLogger()
            Quiet -> QuietLogger
        }
    }

    operator fun Unit.unaryPlus() {
        eager = true
    }

    inline fun CommandFrameworkBuilder.extension(value: CompilerExtension<*>) = value
    inline fun CommandFrameworkBuilder.outputType(value: CompilerOutput) = value
    inline fun CommandFrameworkBuilder.eager() = Unit

    fun create() = CommandFramework(
        outputLogger, extensions.toSet(), eager
    )
}