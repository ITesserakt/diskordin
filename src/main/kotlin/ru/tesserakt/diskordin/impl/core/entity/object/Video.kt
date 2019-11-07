package ru.tesserakt.diskordin.impl.core.entity.`object`


import ru.tesserakt.diskordin.core.data.json.response.VideoResponse
import ru.tesserakt.diskordin.core.entity.`object`.IVideo

class Video(raw: VideoResponse) : IVideo {
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
