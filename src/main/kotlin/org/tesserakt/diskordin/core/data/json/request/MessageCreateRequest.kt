package org.tesserakt.diskordin.core.data.json.request

data class MessageCreateRequest(
    val content: String?,
    val nonce: Long? = null,
    val tts: Boolean? = null,
    val embed: EmbedCreateRequest? = null
) : JsonRequest()