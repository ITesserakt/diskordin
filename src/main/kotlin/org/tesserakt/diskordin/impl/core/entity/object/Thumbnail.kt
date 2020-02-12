package org.tesserakt.diskordin.impl.core.entity.`object`


import org.tesserakt.diskordin.core.data.json.response.ThumbnailResponse
import org.tesserakt.diskordin.core.entity.`object`.IImage

internal class Thumbnail(raw: ThumbnailResponse) : IImage {
    override val url: String? = raw.url
    override val height: Int? = raw.height
    override val width: Int? = raw.width

    override fun toString(): String {
        return "Thumbnail(url=$url, height=$height, width=$width)"
    }
}
