package ru.tesserakt.diskordin.core.data.json.request

data class MessageEditRequest(
    val content: String? = null,
    val embed: EmbedCreateRequest? = null
) : JsonRequest()
