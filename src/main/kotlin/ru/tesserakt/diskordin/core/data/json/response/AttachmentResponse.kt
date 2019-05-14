package ru.tesserakt.diskordin.core.data.json.response


data class AttachmentResponse(
    val id: Long,
    val filename: String,
    val size: Long,
    val url: String,
    val proxy_url: String,
    val height: Int?,
    val width: Int?
) : DiscordResponse()
