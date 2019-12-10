package org.tesserakt.diskordin.impl.core.entity


import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.AttachmentResponse
import org.tesserakt.diskordin.core.entity.IAttachment

internal class Attachment(raw: AttachmentResponse) : IAttachment {
    override val url: String = raw.url

    override val id: Snowflake = raw.id

    override val fileName: String = raw.filename

    override fun toString(): String {
        return StringBuilder("Attachment(")
            .appendln("url='$url', ")
            .appendln("id=$id, ")
            .appendln("fileName='$fileName'")
            .appendln(")")
            .toString()
    }
}