package ru.tesserakt.diskordin.impl.core.client

import com.google.gson.FieldNamingPolicy
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.header
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.eagerSingleton
import org.kodein.di.generic.singleton
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.client.TokenType
import ru.tesserakt.diskordin.util.Loggers
import ru.tesserakt.diskordin.util.ThrowingPolicy

class DiscordClientBuilder private constructor() {
    lateinit var token: String
    lateinit var tokenType: TokenType
    lateinit var globalThrowingPolicy: ThrowingPolicy

    companion object {
        internal lateinit var kodein: Kodein.Module
            private set
        private val logger by Loggers

        operator fun invoke(init: DiscordClientBuilder.() -> Unit): IDiscordClient {
            val builder = DiscordClientBuilder().apply(init)

            logger.debug("Loading http client...")
            val httpClient = HttpClient(OkHttp) {
                install(JsonFeature) {
                    serializer = GsonSerializer {
                        setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    }
                }

                defaultRequest {
                    header("Authorization", "${builder.tokenType.name} ${builder.token}")
                }
            }
            logger.debug("Done")

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