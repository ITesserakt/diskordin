@file:Suppress("NOTHING_TO_INLINE", "unused")

package org.tesserakt.diskordin.commands

import arrow.core.Eval
import arrow.core.extensions.either.monad.flatTap
import arrow.core.extensions.fx
import arrow.core.flatMap
import arrow.core.getOrHandle
import arrow.core.right
import io.github.classgraph.ClassGraph
import mu.KotlinLogging
import org.tesserakt.diskordin.commands.compiler.CommandModuleCompiler
import org.tesserakt.diskordin.commands.compiler.CommandModuleLoader
import org.tesserakt.diskordin.commands.compiler.CompilerExtension
import org.tesserakt.diskordin.commands.compiler.extension.*
import org.tesserakt.diskordin.commands.feature.Feature
import org.tesserakt.diskordin.core.client.IDiscordClient
import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilder
import kotlin.math.ceil

data class CommandFrameworkExtensions(val extraExtensions: Set<CompilerExtension<out Feature<*>>>)

private val defaultExtension = setOf(
    FunctionParametersCompiler(),
    ReturnTypeCompiler(),
    DescriptionCompiler(),
    AliasesCompiler(),
    HiddenCompiler()
)

private val workersCount = 2.coerceAtLeast(
    ceil(
        4.0
            .coerceAtMost(Runtime.getRuntime().availableProcessors() * 0.75) + Runtime.getRuntime()
            .availableProcessors() * 1.25
    ).toInt()
)

private val logger = KotlinLogging.logger("[Discord client]")

private var commandRegistryPrivate = Eval.now(CommandRegistry.EMPTY)

inline fun <F> DiscordClientBuilder<F>.enableCommandFramework(vararg extraExtensions: CompilerExtension<out Feature<*>>) =
    CommandFrameworkExtensions(extraExtensions.toSet())

operator fun CommandFrameworkExtensions.unaryPlus() {
    commandRegistryPrivate = Eval.fx {
        val graph = !Eval.later {
            ClassGraph()
                .enableAnnotationInfo()
                .enableMethodInfo()
                .blacklistModules("java.*", "javax.*", "sun.*", "com.sun.*", "kotlin.*", "kotlinx.*")
        }

        val compiler = CommandModuleCompiler(defaultExtension + extraExtensions)

        !Eval.later { CommandModuleLoader(compiler, graph, workersCount) }
    }.map { loader ->
        loader.load().attempt().unsafeRunSync()
            .flatTap { logger.info(it.toString()).right() }
            .mapLeft { it.localizedMessage }
            .flatMap {
                it.result.mapLeft { err -> it.errorsToString(err) }
            }.getOrHandle { throw IllegalStateException(it) }
    }
}

val IDiscordClient.commandRegistry: CommandRegistry get() = commandRegistryPrivate.memoize().extract()