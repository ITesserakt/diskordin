package org.tesserakt.diskordin.commands.compiler

import arrow.core.sequenceValidated
import arrow.fx.coroutines.release
import arrow.fx.coroutines.resource
import arrow.typeclasses.Semigroup
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.github.classgraph.ScanResult
import mu.KotlinLogging
import org.tesserakt.diskordin.commands.CommandRegistry
import org.tesserakt.diskordin.commands.compiler.CompilerExtension.Constants.IGNORE

@Suppress("unused")
class CommandModuleLoader(
    private val compiler: CommandModuleCompiler,
    private val graph: ClassGraph,
    private val parallelism: Int = 1
) {
    private val logger = KotlinLogging.logger { }
    private val scan = resource {
        logger.debug("Inspecting packages for command modules")
        graph.scan(parallelism)
    } release ScanResult::close

    suspend fun load() = scan use {
        loadToRegistry(it)
    }

    private fun ScanResult.commandModules() =
        allClasses.filter { it.extendsSuperclass("org.tesserakt.diskordin.commands.CommandModule") }
            .filter { it.commands().isNotEmpty() }

    private fun ClassInfo.allowedMethods() = methodInfo
        .filter { !it.isSynthetic && !it.isConstructor && !it.isBridge }

    private fun ClassInfo.commands() =
        allowedMethods().filter { it.hasAnnotation("org.tesserakt.diskordin.commands.Command") }

    private fun loadToRegistry(scan: ScanResult): CompilerSummary {
        val modules = scan.commandModules().toList().filter { !it.hasAnnotation(IGNORE) }
        val compiled = modules.map { compiler.compileModule(it) }.flatten()
        val commands = compiled.map {
            it.validate()
        }.sequenceValidated(Semigroup.nonEmptyList()).map { CommandRegistry(it) }

        return CompilerSummary(modules.size, compiled.size, commands.toEither())
    }
}