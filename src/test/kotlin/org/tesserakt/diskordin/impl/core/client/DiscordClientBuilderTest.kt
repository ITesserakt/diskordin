package org.tesserakt.diskordin.impl.core.client

import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import okhttp3.OkHttpClient
import org.tesserakt.diskordin.rest.withoutRest
import kotlin.time.ExperimentalTime

@ExperimentalTime
@DiscordClientBuilder.InternalTestAPI
class DiscordClientBuilderTest : StringSpec() {
    private var sideEffect: Boolean = false

    private suspend fun DiscordClientBuilder.Companion.test(f: DiscordClientBuilder.() -> Unit = {}) =
        invoke {
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
            DiscordClient.client.tryTake()
        }

        "Single creation of DiscordClientBuilder should not produce error" {
            DiscordClientBuilder.test().withoutRest().shouldBeRight()
        }

        "Twice creation without close should produce error" {
            DiscordClientBuilder.test().withoutRest()
            DiscordClientBuilder.test().withoutRest() shouldBeLeft DiscordClient.AlreadyStarted
        }

        "Twice creation with close should not produce error" {
            val client = DiscordClientBuilder.test().withoutRest()
            client.map { it.logout() }
            DiscordClientBuilder.test().withoutRest().shouldBeRight()
        }
    }
}