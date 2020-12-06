package org.tesserakt.diskordin.commands.compiler

import io.github.classgraph.ClassGraph
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.tesserakt.diskordin.commands.Command
import org.tesserakt.diskordin.commands.CommandContext
import org.tesserakt.diskordin.commands.CommandModule
import org.tesserakt.diskordin.commands.Ignore
import org.tesserakt.diskordin.commands.compiler.extension.FunctionBodyCompiler
import org.tesserakt.diskordin.commands.compiler.extension.FunctionParametersCompiler
import org.tesserakt.diskordin.commands.compiler.extension.ModuleInstanceCompiler
import org.tesserakt.diskordin.commands.compiler.extension.ReturnTypeCompiler

@Suppress("unused")
class Test : CommandModule<CommandContext>() {
    @Command
    fun CommandContext.test() {
    }

    @Suppress("RedundantSuspendModifier")
    @Command
    @Ignore
    suspend fun broken() {
    }

    companion object : Factory {
        override fun create(): CommandModule<*> = Test()
    }
}

class CommandModuleLoaderTest : StringSpec() {
    private val graph: ClassGraph = ClassGraph()
        .acceptPackages("org.tesserakt.diskordin.commands.compiler")
        .enableMethodInfo()
        .enableAnnotationInfo()

    init {
        "Loader should produce error for invalid command" {
            val compiler = CommandModuleCompiler(
                setOf(
                    FunctionBodyCompiler(),
                    ReturnTypeCompiler(),
                    FunctionParametersCompiler(),
                    ModuleInstanceCompiler()
                )
            ) //all persistent compilers
            val loader = CommandModuleLoader(compiler, graph, defaultTestConfig?.threads ?: 1)
            val summary = loader.load()

            assertSoftly(summary) {
                compiledCommands shouldBe 1
                loadedModules shouldBe 1
                result.shouldBeRight()
            }
        }
    }
}