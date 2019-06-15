package ru.tesserakt.diskordin.util

import io.ktor.client.request.forms.FormBuilder
import io.ktor.http.Headers
import io.ktor.http.HeadersBuilder
import io.ktor.util.InternalAPI

internal fun HeadersBuilder.append(name: String, value: String?) = value?.let {
    append(name, it)
}

@UseExperimental(InternalAPI::class)
internal fun <T : Any> FormBuilder.appendNullable(key: String, value: T?, headers: Headers = Headers.Empty) =
    value?.let {
        append(key, value, headers)
    }