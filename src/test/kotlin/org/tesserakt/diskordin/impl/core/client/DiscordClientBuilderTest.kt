package org.tesserakt.diskordin.impl.core.client

import arrow.fx.fix
import org.amshove.kluent.`should not throw`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.`with message`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

private const val token = "NTQ3ND5MTA3NTg1MDA3NjM.123456.UiyyQPPomIp_uzpGgEwhHY" //doesn't work with real bot

internal class DiscordClientBuilderTest {
    @Test
    fun `create instance of client once`() {
        {
            DiscordClientBuilder {
                +token(token)
            }
        } `should not throw` IllegalStateException::class `with message` "Discord client already created"
    }

    @Test
    fun `create instance of client twice should produce error`() {
        DiscordClientBuilder {
            +token(token)
        };
        {
            DiscordClientBuilder {
                +token(token)
            }
        } `should throw` IllegalStateException::class `with message` "Discord client already created"
    }

    @Test
    fun `create client, logout and create again`() {
        val cl = DiscordClientBuilder {
            +token(token)
        }
        cl.logout();
        {
            DiscordClientBuilder {
                +token(token)
            }
        } `should not throw` IllegalStateException::class `with message` "Discord client already created"
    }

    @AfterEach
    fun tearDown() {
        DiscordClient.client.set(null).fix().unsafeRunSync()
    }
}