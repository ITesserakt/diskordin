package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IAttachment
import org.tesserakt.diskordin.impl.core.entity.Attachment


data class AttachmentResponse(
    val id: Snowflake,
    val filename: String,
    val size: Long,
    val url: String,
    val proxy_url: String,
    val height: Int?,
    val width: Int?
) : DiscordResponse<IAttachment, UnwrapContext.EmptyContext>() {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): IAttachment = Attachment(this)
}
