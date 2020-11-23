@file:Suppress("NOTHING_TO_INLINE", "unused")

package org.tesserakt.diskordin.commands.integration

import arrow.core.Eval
import arrow.core.getOrHandle
import arrow.fx.IO
import io.github.classgraph.ClassGraph
import mu.KotlinLogging
import org.tesserakt.diskordin.commands.CommandRegistry
import org.tesserakt.diskordin.commands.compiler.CommandModuleCompiler
import org.tesserakt.diskordin.commands.compiler.CommandModuleLoader
import org.tesserakt.diskordin.commands.compiler.CompilerExtension
import org.tesserakt.diskordin.commands.compiler.extension.*
import org.tesserakt.diskordin.commands.feature.Feature
import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilderScope
import kotlin.math.ceil

private val defaultExtension = setOf(
    FunctionParametersCompiler(),
    ReturnTypeCompiler(),
    FunctionBodyCompiler(),
    DescriptionCompiler(),
    AliasesCompiler(),
    HiddenCompiler(),
    ModuleInstanceCompiler()
)

private val workersCount = 2.coerceAtLeast(
    ceil(
        4.0
            .coerceAtMost(Runtime.getRuntime().availableProcessors() * 0.75) + Runtime.getRuntime()
            .availableProcessors() * 1.25
    ).toInt()
)

private var loggerPrivate: Logger = QuietLogger

inline fun DiscordClientBuilderScope.commandFramework(
    builder: CommandFrameworkBuilder.() -> Unit = { }
) = CommandFrameworkBuilder().apply {
    +resolvers()
    builder()
}.create() to this

suspend operator fun Pair<CommandFramework, DiscordClientBuilderScope>.unaryPlus() {
    loggerPrivate = first.logger

    val graph = ClassGraph()
        .enableAnnotationInfo()
        .enableMethodInfo()
        .blacklistModules("java.*", "javax.*", "sun.*", "com.sun.*", "kotlin.*", "kotlinx.*")

    val compiler = CommandModuleCompiler(defaultExtension + first.extraExtensions)
    val realLogger = KotlinLogging.logger("[Discord client]")
    val loader = CommandModuleLoader(compiler, graph, workersCount)

    val registry: Eval<CommandRegistry> = Eval.always {
        val summary = IO { loader.load() }.unsafeRunSync()

        summary.also { first.logger.logSummary(it) }
        first.logger.logs.forEach(realLogger::info)

        summary.result.mapLeft { IllegalStateException(summary.errorsToString(it)) }.getOrHandle { throw it }
    }

    second.run {
        +install(Commands) {
            Commands(
                first.resolversProvider,
                if (first.eager) Eval.now(registry.value())
                else registry
            )
        }
    }
}

val CompilerExtension<*>.logger get() = loggerPrivate
val Feature<*>.logger get() = loggerPrivate