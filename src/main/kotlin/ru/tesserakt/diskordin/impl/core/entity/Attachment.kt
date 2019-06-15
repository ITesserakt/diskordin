package ru.tesserakt.diskordin.impl.core.entity

import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import ru.tesserakt.diskordin.Diskordin
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.AttachmentResponse
import ru.tesserakt.diskordin.core.entity.IAttachment

class Attachment(raw: AttachmentResponse, override val kodein: Kodein = Diskordin.kodein) : IAttachment {
    override val url: String = raw.url

    override val id: Snowflake = raw.id.asSnowflake()

    override val client: IDiscordClient by instance()

    override val fileName: String = raw.filename
}