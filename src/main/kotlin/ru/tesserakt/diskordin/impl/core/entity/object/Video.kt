package ru.tesserakt.diskordin.impl.core.entity.`object`

import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.json.response.VideoResponse
import ru.tesserakt.diskordin.core.entity.`object`.IVideo

class Video(raw: VideoResponse, override val kodein: Kodein) : IVideo {
    override val url: String? = raw.url
    override val height: Int? = raw.height
    override val width: Int? = raw.width
    override val client: IDiscordClient by instance()
}
