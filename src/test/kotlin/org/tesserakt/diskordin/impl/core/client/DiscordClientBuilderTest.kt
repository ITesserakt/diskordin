package org.tesserakt.diskordin.impl.core.client

import arrow.fx.ForIO
import arrow.fx.fix
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe
import okhttp3.OkHttpClient
import kotlin.time.ExperimentalTime

@ExperimentalTime
@DiscordClientBuilder.InternalTestAPI
class DiscordClientBuilderTest : StringSpec() {
    private var sideEffect: Boolean = false

    private fun DiscordClientBuilder.Companion.test(f: DiscordClientBuilder<ForIO>.() -> Unit = {}) =
        default {
            f()
            +disableTokenVerification()
            +overrideHttpClient {
                sideEffect = true
                OkHttpClient()
            }
        }

    init {
        afterTest {
            sideEffect.shouldBeFalse()

            sideEffect = false
            DiscordClient.client.set(null).fix().unsafeRunSync()
        }

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
}