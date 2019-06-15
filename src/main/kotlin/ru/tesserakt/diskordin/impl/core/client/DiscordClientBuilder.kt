package ru.tesserakt.diskordin.impl.core.client

import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.eagerSingleton
import org.kodein.di.generic.singleton
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.client.TokenType
import ru.tesserakt.diskordin.util.ThrowingPolicy

class DiscordClientBuilder private constructor() {
    lateinit var token: String
    lateinit var tokenType: TokenType
    lateinit var globalThrowingPolicy: ThrowingPolicy

    companion object {
        internal lateinit var kodein: Kodein.Module
            private set

        operator fun invoke(init: DiscordClientBuilder.() -> Unit): IDiscordClient {
            val builder = DiscordClientBuilder().apply(init)
            val httpClient = PredefinedHttpClient(builder.token, builder.tokenType.name).get()
            val client = DiscordClient(
                builder.token,
                builder.tokenType,
                httpClient
            )

            kodein = Kodein.Module("DiscordClient") {
                bind<IDiscordClient>() with eagerSingleton { client }
                bind() from singleton { httpClient }
            }
            return client
        }
    }
}