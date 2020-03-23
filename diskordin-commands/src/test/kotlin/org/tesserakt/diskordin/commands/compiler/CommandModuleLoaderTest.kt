package org.tesserakt.diskordin.commands.compiler

import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.async.async
import io.github.classgraph.ClassGraph
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.tesserakt.diskordin.commands.Command
import org.tesserakt.diskordin.commands.CommandContext
import org.tesserakt.diskordin.commands.CommandModule

@Suppress("unused")
class Test : CommandModule<ForIO, CommandContext<ForIO>>(IO.async()) {
    @Command
    fun test() {
    }

    @Command
    fun broken() {
    }
}

class CommandModuleLoaderTest : StringSpec() {
    private val graph: ClassGraph = ClassGraph()
        .whitelistClasses("org.tesserakt.diskordin.commands.compiler.Test")
        .enableMethodInfo()
        .enableAnnotationInfo()

    init {
        "Loader should produce error for invalid command" {
            val compiler = CommandModuleCompiler(emptyList())
            val loader = CommandModuleLoader(compiler, graph, defaultTestConfig?.threads ?: 1)

            loader.load().attempt().unsafeRunSync() shouldBeRight {
                it.compiledCommands shouldBe 2
                it.loadedModules shouldBe 1
                it.result.shouldBeRight()
            }
        }
    }
}