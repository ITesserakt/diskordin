package org.tesserakt.diskordin.commands.compiler

import arrow.core.nel
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.async.async
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.tesserakt.diskordin.commands.*

class CommandModuleLoaderTest : StringSpec() {
    class Test : CommandModule<ForIO, CommandContext<ForIO>>(IO.async()) {
        @Command
        @Description("Test")
        fun test() {
        }

        @Command
        fun broken() {
        }
    }

    init {
        "Loader should produce error for invalid command" {
            val compiler = CommandModuleCompiler(emptyList())
            val loader =
                CommandModuleLoader("org.tesserakt.diskordin.commands.compiler", defaultTestConfig?.threads, compiler)

            loader.load().attempt().unsafeRunSync() shouldBeRight {
                it.compiledCommands shouldBe 2
                it.loadedModules shouldBe 1
                it.result shouldBeLeft ValidationError.Empty("description").nel()
            }
        }
    }
}