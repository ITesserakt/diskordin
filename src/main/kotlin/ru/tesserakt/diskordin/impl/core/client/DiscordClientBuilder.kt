package ru.tesserakt.diskordin.impl.core.client

import org.koin.Logger.slf4jLogger
import org.koin.core.Koin
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.client.TokenType

class DiscordClientBuilder private constructor() {
    var token: String = "Invalid token"
    var tokenType: TokenType = TokenType.Bot

    companion object {
        operator fun invoke(init: DiscordClientBuilder.() -> Unit): IDiscordClient {
            val builder = DiscordClientBuilder().apply(init)

            return setupKoin(builder).get()
        }

        private fun setupKoin(builder: DiscordClientBuilder): Koin {
            val koin = startKoin {
                slf4jLogger(Level.DEBUG)
                fileProperties()
            }.koin

            val token = koin.getProperty("token", builder.token)
            val tokenType = builder.tokenType

            loadKoinModules(
                module {
                    single { PredefinedHttpClient(token, tokenType.name).get() }
                    single<IDiscordClient> { DiscordClient(token, tokenType, get()) }
                }
            )
            return koin
        }
    }
}