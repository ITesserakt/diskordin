@file:Suppress("NOTHING_TO_INLINE", "unused")

package org.tesserakt.diskordin.commands.integration

import arrow.fx.typeclasses.Async
import org.tesserakt.diskordin.commands.compiler.CompilerExtension
import org.tesserakt.diskordin.commands.feature.Feature
import org.tesserakt.diskordin.commands.integration.CompilerOutput.*
import org.tesserakt.diskordin.commands.resolver.ResolversProvider
import org.tesserakt.diskordin.core.entity.builder.RequestBuilder

@RequestBuilder
class CommandFrameworkBuilder<F>(val A: Async<F>) {
    private val extensions = mutableListOf<CompilerExtension<out Feature<*>>>()
    private var outputLogger: Logger = SummaryLogger()
    private var eager = false
    private var resolversProvider = ResolversProvider(emptyMap())

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

    operator fun <F> ResolversCollector<F>.unaryPlus() {
        this@CommandFrameworkBuilder.resolversProvider = ResolversProvider(resolvers)
    }

    inline fun CommandFrameworkBuilder<F>.extension(value: CompilerExtension<*>) = value
    inline fun CommandFrameworkBuilder<F>.outputType(value: CompilerOutput) = value
    inline fun CommandFrameworkBuilder<F>.eager() = Unit
    inline fun <F> CommandFrameworkBuilder<F>.resolvers(collector: ResolversCollector<F>.() -> Unit = {}) =
        ResolversCollector(A).apply(collector)

    fun create() = CommandFramework(
        outputLogger, extensions.toSet(), eager, resolversProvider
    )
}