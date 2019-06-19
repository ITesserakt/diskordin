package ru.tesserakt.diskordin.util

import io.ktor.client.request.forms.FormBuilder
import io.ktor.http.Headers
import io.ktor.http.HeadersBuilder
import io.ktor.util.InternalAPI

internal fun HeadersBuilder.append(name: String, value: String?) = value?.let {
    append(name, it)
}

@Suppress("NOTHING_TO_INLINE")
@UseExperimental(InternalAPI::class)
internal inline fun <T : Any> FormBuilder.appendNullable(key: String, value: T?, headers: Headers = Headers.Empty) =
    value?.let {
        append(key, value, headers)
    }