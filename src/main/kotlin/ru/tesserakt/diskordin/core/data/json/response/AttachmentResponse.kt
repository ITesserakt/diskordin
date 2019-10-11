package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.impl.core.entity.Attachment


data class AttachmentResponse(
    val id: Long,
    val filename: String,
    val size: Long,
    val url: String,
    val proxy_url: String,
    val height: Int?,
    val width: Int?
) : DiscordResponse() {
    fun unwrap() = Attachment(this)
}
