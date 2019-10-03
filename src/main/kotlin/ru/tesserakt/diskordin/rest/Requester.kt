package ru.tesserakt.diskordin.rest


import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HeadersBuilder
import io.ktor.http.contentType
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.slf4j.Logger
import ru.tesserakt.diskordin.core.data.json.request.JsonRequest
import ru.tesserakt.diskordin.util.Loggers

internal class Requester(val route: Route) : KoinComponent {
    private val httpClient: HttpClient by inject()
    private val logger: Logger by Loggers
    private val apiURL = getKoin().getProperty("API_url", "")

    private var headersInit: (HeadersBuilder.() -> Unit)? = null
    private var paramsInit: Map<String, *>? = null

    fun additionalHeaders(init: HeadersBuilder.() -> Unit) = apply {
        headersInit = init
    }

    fun queryParams(init: List<Pair<String, *>>) = apply {
        paramsInit = init.toMap()
    }

    internal suspend inline fun <reified T : Any> resolve(
        body: Any? = null
    ): T = httpClient.request {
        logger.debug("Call to ${route.urlTemplate}")
        url("$apiURL${route.urlTemplate}")

        headersInit?.let { headers(it) }
        paramsInit?.forEach { p1, p2 -> parameter(p1, p2.toString()) }

        body?.let {
            if (body is JsonRequest)
                contentType(ContentType.Application.Json)
            this.body = body
        }
        method = route.httpMethod
    }
}