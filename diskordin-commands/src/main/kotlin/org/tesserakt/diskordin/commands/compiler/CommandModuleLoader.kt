package org.tesserakt.diskordin.commands.compiler

import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.ValidatedPartialOf
import arrow.core.extensions.list.traverse.sequence
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.extensions.validated.applicative.applicative
import arrow.core.extensions.validated.traverse.traverse
import arrow.core.fix
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.monad.monad
import arrow.mtl.StateT
import arrow.mtl.extensions.fx
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
    private val specifiedPackage: String? = null,
    private val parallelism: Int? = null,
    private val compiler: CommandModuleCompiler
) {
    private val logger = KotlinLogging.logger { }

    private val graph = ClassGraph()
        .blacklistModules("java.*", "javax.*", "sun.*", "com.sun.*", "kotlin.*", "kotlinx.*", "io.arrow-kt.*")
        .enableAnnotationInfo()
        .enableMethodInfo()
        .disableJarScanning()
        .also {
            if (specifiedPackage != null)
                it.whitelistPackages(specifiedPackage)
        }

    fun load() = IO {
        logger.debug("Inspecting packages for command modules")
        if (parallelism != null) graph.scan(parallelism)
        else graph.scan()
    }.flatMap {
        val summary = loadToRegistry(it)
        it.close()
        summary
    }

    private fun ScanResult.commandModules() =
        allClasses.filter { it.extendsSuperclass("org.tesserakt.diskordin.commands.CommandModule") }
            .filter { it.commands().isNotEmpty() }

    private fun ClassInfo.allowedMethods() = this.methodInfo
        .filter { !it.isSynthetic && !it.isConstructor && !it.isBridge }

    private fun ClassInfo.commands() =
        allowedMethods().filter { it.hasAnnotation("org.tesserakt.diskordin.commands.Command") }

    private fun loadToRegistry(scan: ScanResult) = StateT.fx<CompilerSummary, ForIO, CompilerSummary>(IO.monad()) {
        val modules = scan.commandModules().toList()
        !StateT.modify<CompilerSummary, ForIO>(IO.applicative()) { it.copy(loadedModules = modules.size) }

        val compiled = modules.flatMap { compiler.compileModule(it) }
        !StateT.modify<CompilerSummary, ForIO>(IO.applicative()) { it.copy(compiledCommands = compiled.size) }

        val commands = compiled.map {
            it.validate(CommandBuilder.Validator.AccumulateErrors, Validated.traverse())
        }.sequence<ValidatedPartialOf<NonEmptyList<ValidationError>>, CommandObject>(
            Validated.applicative(NonEmptyList.semigroup())
        ).fix().map { validCommands ->
            CommandRegistry(CommandBuilder.Validator.AccumulateErrors, Validated.traverse()) {
                validCommands.fix().forEach { +it }
            }
        }

        !StateT.inspect<CompilerSummary, ForIO, CompilerSummary>(IO.applicative()) {
            it.copy(result = commands.toEither())
        }
    }.runA(IO.monad(), CompilerSummary())
}