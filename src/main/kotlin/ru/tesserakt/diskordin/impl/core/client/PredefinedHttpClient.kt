package ru.tesserakt.diskordin.impl.core.client

import com.google.gson.FieldNamingPolicy
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.*
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.request.header
import io.ktor.util.KtorExperimentalAPI
import ru.tesserakt.diskordin.util.Loggers

internal class PredefinedHttpClient(private val token: String, private val tokenType: String) {
    private val logger = Loggers("[HTTP client]")

    @KtorExperimentalAPI
    fun get() = get(CIO) {}

    @KtorExperimentalAPI
    inline fun <reified T : HttpClientEngineConfig> get(
        httpEngine: HttpClientEngineFactory<T>,
        crossinline engineConfig: T.() -> Unit
    ) = HttpClient(httpEngine) {
        install(JsonFeature) {
            serializer = GsonSerializer {
                setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                disableHtmlEscaping()
            }
        }

        install(WebSockets)

        engine { engineConfig() }

        defaultRequest {
            header("Authorization", "$tokenType $token")
        }

        HttpResponseValidator {
            validateResponse {
                val status = it.status.value
                when (status) {
                    in 200..299 -> {
                        val ping = it.responseTime.timestamp - it.requestTime.timestamp
                        logger.debug("Successfully called to ${it.call.request.url} with time: $ping ms")
                    }
                    in 300..399 -> throw RedirectResponseException(it)
                    in 400..499 -> throw ClientRequestException(it)
                    in 500..599 -> throw ServerResponseException(it)
                }

                if (status >= 600) throw ResponseException(it)
            }
        }
    }
}