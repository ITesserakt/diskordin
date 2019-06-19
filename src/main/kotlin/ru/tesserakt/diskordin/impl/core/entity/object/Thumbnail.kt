package ru.tesserakt.diskordin.impl.core.entity.`object`


import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.json.response.ThumbnailResponse
import ru.tesserakt.diskordin.core.entity.`object`.IImage

class Thumbnail(raw: ThumbnailResponse) : IImage {
    override val url: String? = raw.url
    override val height: Int? = raw.height
    override val width: Int? = raw.width

}
