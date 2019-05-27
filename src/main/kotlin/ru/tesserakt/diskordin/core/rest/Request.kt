package ru.tesserakt.diskordin.core.rest

import arrow.core.Try
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.HeadersBuilder
import io.ktor.http.contentType
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import org.slf4j.Logger
import ru.tesserakt.diskordin.core.client.Diskordin
import ru.tesserakt.diskordin.util.Loggers
import io.ktor.http.HttpMethod.Companion as Method

internal class Request(override val kodein: Kodein, val route: Route) : KodeinAware {
    val httpClient: HttpClient by instance()
    val logger: Logger by Loggers

    var headersInit: (HeadersBuilder.() -> Unit)? = null
    var paramsInit: Map<String, *>? = null

    fun additionalHeaders(init: HeadersBuilder.() -> Unit) = apply {
        headersInit = init
    }

    fun queryParams(vararg init: Pair<String, *>) = apply {
        paramsInit = init.toMap()
    }

    internal suspend inline fun <reified T : Any> resolve(
        body: Any? = null,
        type: ContentType = ContentType.Application.Json
    ) = Try {
        logger.debug("Call to ${route.urlTemplate}")
        httpClient.request<T>(HttpRequestBuilder().apply {
            url("${Diskordin.API_URL}${route.urlTemplate}")

            headersInit?.let { headers(it) }
            paramsInit?.forEach { p1, p2 -> parameter(p1, p2.toString()) }

            body?.let {
                this.body = it
                contentType(type)
            }
            method = route.httpMethod
        })
    }
}