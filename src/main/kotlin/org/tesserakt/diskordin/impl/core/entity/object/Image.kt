package org.tesserakt.diskordin.impl.core.entity.`object`


import org.tesserakt.diskordin.core.data.json.response.ImageResponse
import org.tesserakt.diskordin.core.entity.`object`.IImage

internal class Image(raw: ImageResponse) : IImage {
    override val url: String? = raw.url
    override val height: Int? = raw.height
    override val width: Int? = raw.width

    override fun toString(): String {
        return "Image(url=$url, height=$height, width=$width)"
    }
}