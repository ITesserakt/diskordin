package ru.tesserakt.diskordin.util

import io.ktor.http.HeadersBuilder

fun HeadersBuilder.append(name: String, value: String?) = value?.let {
    append(name, it)
}