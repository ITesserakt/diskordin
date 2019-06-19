package ru.tesserakt.diskordin.impl.core.rest


import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.http.HeadersBuilder
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.slf4j.Logger
import ru.tesserakt.diskordin.util.Loggers
import io.ktor.http.HttpMethod.Companion as Method

internal class Requester(val route: Route) : KoinComponent {
    private val httpClient: HttpClient by inject()
    private val logger: Logger by Loggers
    private val api_url = getKoin().getProperty("API_url", "")

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
        url("$api_url${route.urlTemplate}")

        headersInit?.let { headers(it) }
        paramsInit?.forEach { p1, p2 -> parameter(p1, p2.toString()) }

        body?.let {
            this.body = it
        }
        method = route.httpMethod
    }
}