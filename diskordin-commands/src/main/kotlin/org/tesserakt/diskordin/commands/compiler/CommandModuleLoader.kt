package org.tesserakt.diskordin.commands.compiler

import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.ValidatedPartialOf
import arrow.core.extensions.list.traverse.sequence
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.extensions.validated.applicative.applicative
import arrow.core.extensions.validated.traverse.traverse
import arrow.core.fix
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.just
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.github.classgraph.ScanResult
import mu.KotlinLogging
import org.tesserakt.diskordin.commands.CommandBuilder
import org.tesserakt.diskordin.commands.CommandObject
import org.tesserakt.diskordin.commands.CommandRegistry
import org.tesserakt.diskordin.commands.ValidationError

@Suppress("unused")
class CommandModuleLoader(
    private val compiler: CommandModuleCompiler,
    private val specifiedPackage: String? = null,
    private val parallelism: Int? = null
) {
    private val logger = KotlinLogging.logger { }

    private val graph = ClassGraph()
        .blacklistModules("java.*", "javax.*", "sun.*", "com.sun.*", "kotlin.*", "kotlinx.*")
        .enableAnnotationInfo()
        .enableMethodInfo()
        .disableJarScanning()
        .also {
            if (specifiedPackage != null) it.whitelistPackages(specifiedPackage)
        }

    fun load() = IO {
        logger.debug("Inspecting packages for command modules")
        if (parallelism != null) graph.scan(parallelism)
        else graph.scan()
    }.flatMap(::loadToRegistry)

    private fun ScanResult.commandModules() =
        allClasses.filter { it.extendsSuperclass("org.tesserakt.diskordin.commands.CommandModule") }
            .filter { it.commands().isNotEmpty() }

    private fun ClassInfo.allowedMethods() = this.methodInfo
        .filter { !it.isSynthetic && !it.isConstructor && !it.isBridge }

    private fun ClassInfo.commands() =
        allowedMethods().filter { it.hasAnnotation("org.tesserakt.diskordin.commands.Command") }

    private fun loadToRegistry(scan: ScanResult): IO<CompilerSummary> {
        val modules = scan.commandModules().toList()
        val compiled = modules.flatMap { compiler.compileModule(it) }
        val commands = compiled.map {
            it.validate(CommandBuilder.Validator.AccumulateErrors, Validated.traverse())
        }.sequence<ValidatedPartialOf<NonEmptyList<ValidationError>>, CommandObject>(
            Validated.applicative(NonEmptyList.semigroup())
        ).fix().map { CommandRegistry(it.fix()) }

        return CompilerSummary(modules.size, compiled.size, commands.toEither()).just()
    }
}