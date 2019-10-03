package ru.tesserakt.diskordin.impl.core.client

import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.koin.Logger.slf4jLogger
import org.koin.core.Koin
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.client.TokenType
import kotlin.coroutines.CoroutineContext

class DiscordClientBuilder private constructor() {
    lateinit var token: String
    var tokenType: TokenType = TokenType.Bot
    internal lateinit var httpClient: HttpClient
    var gatewayContext: CoroutineContext = Dispatchers.Default + Job()

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
                    single {
                        if (builder::httpClient.isInitialized)
                            builder.httpClient
                        else
                            PredefinedHttpClient(token, tokenType.name).get()
                    }
                    single<IDiscordClient> { DiscordClient(token, tokenType, get()) }
                }
            )
            koin.setProperty("gatewayContext", builder.gatewayContext)
            return koin
        }
    }
}