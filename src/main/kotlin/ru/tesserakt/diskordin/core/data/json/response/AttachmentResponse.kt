package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.core.entity.IAttachment
import ru.tesserakt.diskordin.impl.core.entity.Attachment


data class AttachmentResponse(
    val id: Long,
    val filename: String,
    val size: Long,
    val url: String,
    val proxy_url: String,
    val height: Int?,
    val width: Int?
) : DiscordResponse<IAttachment>() {
    override fun unwrap(vararg params: Any): IAttachment = Attachment(this)
}
