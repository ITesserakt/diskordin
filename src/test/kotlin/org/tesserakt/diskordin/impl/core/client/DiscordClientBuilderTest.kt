package org.tesserakt.diskordin.impl.core.client

import arrow.fx.ForIO
import arrow.fx.fix
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalTime
@DiscordClientBuilder.InternalTestAPI
class DiscordClientBuilderTest : StringSpec() {
    private fun DiscordClientBuilder.Companion.test(f: DiscordClientBuilder<ForIO>.() -> Unit = {}) =
        default {
            f()
            +disableTokenVerification()
        }

    init {
        //to check whether http client started
        defaultTestConfig = TestCaseConfig(timeout = 1.seconds)

        "Single creation of DiscordClientBuilder should not produce error" {
            shouldNotThrow<IllegalStateException> {
                DiscordClientBuilder.test()
            }
        }

        "Twice creation without close should produce error" {
            DiscordClientBuilder.test()
            shouldThrow<IllegalStateException> {
                DiscordClientBuilder.test()
            }.message shouldBe "Discord client already created"
        }

        "Twice creation with close should not produce error" {
            val client = DiscordClientBuilder.test()
            client.logout()
            shouldNotThrow<IllegalStateException> {
                DiscordClientBuilder.test()
            }
        }
    }

    override fun afterTest(testCase: TestCase, result: TestResult) {
        DiscordClient.client.set(null).fix().unsafeRunSync()
    }
}