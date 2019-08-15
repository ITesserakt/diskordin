package ru.tesserakt.diskordin.rest

import io.ktor.http.HttpMethod

internal data class Route(
    val httpMethod: HttpMethod,
    val urlTemplate: String
) {
    companion object {
        fun get(urlTemplate: String) =
            Route(HttpMethod.Get, urlTemplate)

        fun put(urlTemplate: String) =
            Route(HttpMethod.Put, urlTemplate)

        fun post(urlTemplate: String) =
            Route(HttpMethod.Post, urlTemplate)

        fun patch(urlTemplate: String) =
            Route(HttpMethod.Patch, urlTemplate)

        fun delete(urlTemplate: String) =
            Route(HttpMethod.Delete, urlTemplate)
    }

    fun newRequest() = Requester(this)
}