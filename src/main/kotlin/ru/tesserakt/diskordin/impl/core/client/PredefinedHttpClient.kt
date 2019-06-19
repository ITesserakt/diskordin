package ru.tesserakt.diskordin.impl.core.client

import com.google.gson.FieldNamingPolicy
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.*
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.header
import okhttp3.Cache
import ru.tesserakt.diskordin.util.Loggers
import java.io.File

internal class PredefinedHttpClient(private val token: String, private val tokenType: String) {
    private val logger = Loggers("HTTP client loader")

    fun get() = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = GsonSerializer {
                setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            }
        }

        engine {
            config {
                cache(Cache(File("F:\\tesserakt\\IdeaProjects\\discord-api\\src\\main\\resources"), 10 * 1024 * 1024))
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
    }
}