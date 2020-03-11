package org.tesserakt.diskordin.commands

import arrow.core.Validated
import arrow.core.extensions.validated.foldable.firstOption
import arrow.core.extensions.validated.traverse.traverse
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCaseConfig
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.string.shouldMatch
import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilder
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalTime
@DiscordClientBuilder.InternalTestAPI
class CommandRegistryTest : FunSpec() {
    init {
        defaultTestConfig = TestCaseConfig(timeout = 1.seconds, threads = 1)

        test("Empty registry should not contain any commands") {
            val client = DiscordClientBuilder.default {
                +disableTokenVerification()
            }

            val emptyRegistry = client.commandRegistry

            emptyRegistry.shouldBeEmpty()

            afterTest {
                client.logout()
            }
        }

        context("Non-empty registry") {
            lateinit var first: CommandObject
            lateinit var second: CommandObject

            val client = DiscordClientBuilder.default {
                +disableTokenVerification()
                +commandRegistry(CommandBuilder.Validator.AccumulateErrors, Validated.traverse()) {
                    first = command {
                        +name("Test")
                        +description("Test command")
                    }.firstOption().orNull()!!
                    +first

                    second = command {
                        +name("Test2")
                        +description("Test command")
                        +hide()
                    }.firstOption().orNull()!!
                    +second
                }
            }

            val registry = client.commandRegistry

            test("Registry should not provide hidden commands") {
                registry shouldNotContain second
            }

            test("Registry should provide all public commands") {
                registry shouldContain first
            }

            test("Registry should show it content") {
                registry.toString() shouldMatch Regex("""CommandRegistry \[.*] with 1 hidden commands""")
            }

            afterTest {
                client.logout()
            }
        }
    }
}