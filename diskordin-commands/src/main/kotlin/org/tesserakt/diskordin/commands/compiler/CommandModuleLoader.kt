package org.tesserakt.diskordin.commands.compiler

import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.extensions.list.traverse.sequence
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.extensions.validated.applicative.applicative
import arrow.core.extensions.validated.traverse.traverse
import arrow.core.fix
import arrow.fx.coroutines.ForkConnected
import arrow.fx.coroutines.release
import arrow.fx.coroutines.resource
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.github.classgraph.ScanResult
import mu.KotlinLogging
import org.tesserakt.diskordin.commands.CommandBuilder
import org.tesserakt.diskordin.commands.CommandRegistry
import org.tesserakt.diskordin.commands.compiler.CompilerExtension.Constants.IGNORE

@Suppress("unused")
class CommandModuleLoader(
    private val compiler: CommandModuleCompiler,
    private val graph: ClassGraph,
    private val parallelism: Int = 1
) {
    private val logger = KotlinLogging.logger { }

    suspend fun load() = resource {
        ForkConnected {
            logger.debug("Inspecting packages for command modules")
            graph.scan(parallelism)
        }
    } release {
        it.join().close()
    } use {
        loadToRegistry(it.join())
    }

    private fun ScanResult.commandModules() =
        allClasses.filter { it.extendsSuperclass("org.tesserakt.diskordin.commands.CommandModule") }
            .filter { it.commands().isNotEmpty() }

    private fun ClassInfo.allowedMethods() = this.methodInfo
        .filter { !it.isSynthetic && !it.isConstructor && !it.isBridge }

    private fun ClassInfo.commands() =
        allowedMethods().filter { it.hasAnnotation("org.tesserakt.diskordin.commands.Command") }

    private fun loadToRegistry(scan: ScanResult): CompilerSummary {
        val modules = scan.commandModules().toList().filter { !it.hasAnnotation(IGNORE) }
        val compiled = modules.map { compiler.compileModule(it) }.flatten()
        val commands = compiled.map {
            it.validate(CommandBuilder.Validator.AccumulateErrors, Validated.traverse())
        }.sequence(Validated.applicative(NonEmptyList.semigroup())).fix().map { CommandRegistry(it.fix()) }

        return CompilerSummary(modules.size, compiled.size, commands.toEither())
    }
}