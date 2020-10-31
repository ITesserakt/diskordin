package org.tesserakt.diskordin.impl.core.client

import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import org.tesserakt.diskordin.rest.withoutRest
import kotlin.time.ExperimentalTime

@ExperimentalTime
@DiscordClientBuilderScope.InternalTestAPI
class DiscordClientBuilderTest : StringSpec() {
    private suspend fun DiscordClientBuilder.test(f: DiscordClientBuilderScope.() -> Unit = {}) =
        withoutRest {
            f()
            +disableTokenVerification()
        }

    init {
        afterTest {
            DiscordClient.client.tryTake()
        }

        "Single creation of DiscordClientBuilder should not produce error" {
            DiscordClientBuilder.test().shouldBeRight()
        }

        "Twice creation without close should produce error" {
            DiscordClientBuilder.test()
            DiscordClientBuilder.test() shouldBeLeft DiscordClient.AlreadyStarted
        }

        "Twice creation with close should not produce error" {
            val client = DiscordClientBuilder.test()
            client.map { it.logout() }
            DiscordClientBuilder.test().shouldBeRight()
        }
    }
}