package org.tesserakt.diskordin.impl.core.client

import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.assertions.arrow.either.shouldNotBeLeft
import io.kotest.core.spec.style.StringSpec
import org.tesserakt.diskordin.core.client.InternalTestAPI
import org.tesserakt.diskordin.rest.WithoutRest
import kotlin.time.ExperimentalTime

@ExperimentalTime
@InternalTestAPI
class DiscordClientBuilderTest : StringSpec() {
    private suspend fun DiscordClientBuilder.test(f: DiscordClientBuilderScope.() -> Unit = {}) =
        this[WithoutRest] {
            f()
            +disableTokenVerification()
        }

    init {
        afterTest { DiscordClient.removeState() }

        "Single creation of DiscordClientBuilder should not produce error" {
            DiscordClientBuilder.test().shouldBeRight()
            DiscordClient.getInitialized().shouldNotBeLeft()
        }

        "Twice creation without close should produce error" {
            DiscordClientBuilder.test()
            DiscordClientBuilder.test() shouldBeLeft DiscordClient.AlreadyStarted
            DiscordClient.getInitialized().shouldNotBeLeft()
        }

        "Twice creation with close should not produce error" {
            val client = DiscordClientBuilder.test()
            client.map { it.logout() }
            DiscordClientBuilder.test().shouldBeRight()
            DiscordClient.getInitialized().shouldNotBeLeft()
        }
    }
}