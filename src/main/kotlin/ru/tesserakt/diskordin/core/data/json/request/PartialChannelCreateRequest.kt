package ru.tesserakt.diskordin.core.data.json.request


data class PartialChannelCreateRequest(
    val name: String,
    val type: Int
) : JsonRequest()
