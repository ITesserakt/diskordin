@file:Suppress("NOTHING_TO_INLINE", "unused")

package org.tesserakt.diskordin.commands.integration

import arrow.core.Eval
import arrow.core.extensions.either.monad.flatTap
import arrow.core.flatMap
import arrow.core.getOrHandle
import arrow.core.right
import io.github.classgraph.ClassGraph
import mu.KotlinLogging
import org.tesserakt.diskordin.commands.CommandRegistry
import org.tesserakt.diskordin.commands.compiler.CommandModuleCompiler
import org.tesserakt.diskordin.commands.compiler.CommandModuleLoader
import org.tesserakt.diskordin.commands.compiler.CompilerExtension
import org.tesserakt.diskordin.commands.compiler.extension.*
import org.tesserakt.diskordin.commands.feature.Feature
import org.tesserakt.diskordin.core.client.IDiscordClient
import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilder
import kotlin.math.ceil

private val defaultExtension = setOf(
    FunctionParametersCompiler(),
    ReturnTypeCompiler(),
    FunctionBodyCompiler(),
    DescriptionCompiler(),
    AliasesCompiler(),
    HiddenCompiler(),
    ModuleCompiler()
)

private val workersCount = 2.coerceAtLeast(
    ceil(
        4.0
            .coerceAtMost(Runtime.getRuntime().availableProcessors() * 0.75) + Runtime.getRuntime()
            .availableProcessors() * 1.25
    ).toInt()
)

private var commandRegistryPrivate = Eval.now(CommandRegistry.EMPTY)
private var loggerPrivate: Logger = QuietLogger

inline fun <F> DiscordClientBuilder<F>.enableCommandFramework(builder: CommandFrameworkBuilder.() -> Unit = {}) =
    CommandFrameworkBuilder().apply(builder).create()

operator fun CommandFramework.unaryPlus() {
    loggerPrivate = logger

    commandRegistryPrivate = Eval.later {
        val graph = ClassGraph()
            .enableAnnotationInfo()
            .enableMethodInfo()
            .blacklistModules("java.*", "javax.*", "sun.*", "com.sun.*", "kotlin.*", "kotlinx.*")

        val compiler = CommandModuleCompiler(defaultExtension + extraExtensions)

        CommandModuleLoader(compiler, graph, workersCount)
    }.map { loader ->
        val summary = loader.load().attempt().unsafeRunSync()
        val realLogger = KotlinLogging.logger("[Discord client]")

        summary.flatTap { logger.logSummary(it).right() }
            .map { logger.logs }
            .map { it.forEach(realLogger::info) }

        summary.mapLeft { it }
            .flatMap { it.result.mapLeft { msg -> IllegalStateException(it.errorsToString(msg)) } }
            .getOrHandle { throw it }
    }.memoize()

    if (eager) commandRegistryPrivate.extract()
}

val IDiscordClient.commandRegistry: CommandRegistry get() = commandRegistryPrivate.extract()
val CompilerExtension<*>.logger get() = loggerPrivate
val Feature<*>.logger get() = loggerPrivate