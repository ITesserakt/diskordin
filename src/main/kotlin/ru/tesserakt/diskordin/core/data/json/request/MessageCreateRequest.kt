package ru.tesserakt.diskordin.core.data.json.request

import kotlinx.io.core.Input

data class MessageCreateRequest(
    val content: String,
    val nonce: Long? = null,
    val tts: Boolean? = null,
    val file: Input? = null,
    val embed: EmbedCreateRequest? = null
) : JsonRequest()