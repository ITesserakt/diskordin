package org.tesserakt.diskordin.impl.core.entity.`object`


import org.tesserakt.diskordin.core.data.json.response.VideoResponse
import org.tesserakt.diskordin.core.entity.`object`.IVideo

internal class Video(raw: VideoResponse) : IVideo {
    override val url: String? = raw.url
    override val height: Int? = raw.height
    override val width: Int? = raw.width
    override fun toString(): String {
        return StringBuilder("Video(")
            .appendln("url=$url, ")
            .appendln("height=$height, ")
            .appendln("width=$width")
            .appendln(")")
            .toString()
    }
}
