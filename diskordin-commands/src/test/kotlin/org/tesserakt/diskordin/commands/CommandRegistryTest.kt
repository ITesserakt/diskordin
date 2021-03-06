package org.tesserakt.diskordin.commands

import arrow.core.getOrHandle
import arrow.core.orNull
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldMatch
import org.tesserakt.diskordin.commands.feature.HiddenFeature
import org.tesserakt.diskordin.commands.integration.Commands
import org.tesserakt.diskordin.commands.integration.commandFramework
import org.tesserakt.diskordin.commands.integration.unaryPlus
import org.tesserakt.diskordin.core.client.InternalTestAPI
import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilder
import org.tesserakt.diskordin.rest.WithoutRest
import kotlin.time.ExperimentalTime

@InternalTestAPI
@ExperimentalTime
class CommandRegistryTest : FunSpec() {
    init {
        test("Empty registry should not contain any commands") {
            val client = DiscordClientBuilder[WithoutRest] {
                +disableTokenVerification()
                +commandFramework {
                    +specifySearch { rejectPackages("org.tesserakt.diskordin") }
                }
            }.getOrHandle { error(it) }

            val commands = client.context[Commands]
            commands.shouldNotBeNull()

            commands.registry.value().shouldBeEmpty()
        }

        context("Non-empty registry") {
            lateinit var first: CommandObject
            lateinit var second: CommandObject

            val registry = CommandRegistry {
                first = command {
                    +name("Test 1")
                }.orNull()!!
                +first

                second = command {
                    +name("Test 2")
                    +features(setOf(HiddenFeature()))
                }.orNull()!!
                +second
            }

            test("Registry should not provide hidden commands") {
                registry shouldNotContain second
            }

            test("Registry should provide all public commands") {
                registry shouldContain first
            }

            test("Registry should show it content") {
                registry.toString() shouldMatch Regex("""CommandRegistry contains 1 commands with 1 hidden ones""")
            }
        }
    }
}