package org.tesserakt.diskordin.impl.core.client

import arrow.fx.fix
import org.amshove.kluent.`should not throw`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.`with message`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit

@DiscordClientBuilder.InternalTestAPI
internal class DiscordClientBuilderTest {
    @Test
    @Timeout(1, unit = TimeUnit.SECONDS)
    fun `create instance of client once`() {
        {
            DiscordClientBuilder.default { +disableTokenVerification() }
        } `should not throw` IllegalStateException::class `with message` "Discord client already created"
    }

    @Test
    @Timeout(1, unit = TimeUnit.SECONDS)
    fun `create instance of client twice should produce error`() {
        DiscordClientBuilder.default { +disableTokenVerification() };
        {
            DiscordClientBuilder.default { +disableTokenVerification() }
        } `should throw` IllegalStateException::class `with message` "Discord client already created"
    }

    @Test
    @Timeout(1, unit = TimeUnit.SECONDS)
    fun `create client, logout and create again`() {
        val cl = DiscordClientBuilder.default { +disableTokenVerification() }
        cl.logout();
        {
            DiscordClientBuilder.default { +disableTokenVerification() }
        } `should not throw` IllegalStateException::class `with message` "Discord client already created"
    }

    @AfterEach
    fun tearDown() {
        DiscordClient.client.set(null).fix().unsafeRunSync()
    }
}