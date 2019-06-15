package ru.tesserakt.diskordin.impl.core.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.HttpResponseValidator
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.header

internal class PredefinedHttpClient(private val token: String, private val tokenType: String) {
    private val logger = Loggers("HTTP client loader")

    fun get() = HttpClient(OkHttp) {
        logger.debug("Started loading HTTP client...")
        install(JsonFeature) {
            serializer = GsonSerializer {
                setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            }
        }

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
        logger.debug("Done.")
    }
}