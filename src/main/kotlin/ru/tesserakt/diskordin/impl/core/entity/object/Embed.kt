package ru.tesserakt.diskordin.impl.core.entity.`object`

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.json.response.*
import ru.tesserakt.diskordin.core.entity.`object`.IEmbed
import ru.tesserakt.diskordin.core.entity.`object`.IImage
import ru.tesserakt.diskordin.core.entity.`object`.IVideo
import java.awt.Color
import java.time.Instant
import java.time.format.DateTimeFormatter

class Embed(raw: EmbedResponse, override val kodein: Kodein) : IEmbed {
    override val title: String? = raw.title

    override val type: String? = raw.type

    override val description: String? = raw.description

    override val url: String? = raw.url

    override val timestamp: Instant? = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(raw.timestamp, Instant::from)

    override val color: Color = Color(raw.color ?: 0)

    override val footer: IEmbed.IFooter? = raw.footer?.let { Footer(it) }

    class Footer(raw: FooterResponse) : IEmbed.IFooter {
        override val text: String = raw.text
        override val iconUrl: String? = raw.icon_url
    }

    override val image: IImage? = raw.image?.let { Image(it, kodein) }

    override val thumbnail: IImage? = raw.thumbnail?.let { Thumbnail(it, kodein) }

    override val video: IVideo? = raw.video?.let { Video(it, kodein) }

    override val provider: IEmbed.IProvider? = raw.provider?.let { Provider(it) }

    class Provider(raw: ProviderResponse) : IEmbed.IProvider {
        override val name: String? = raw.name
        override val url: String? = raw.url
    }

    override val author: IEmbed.IAuthor? = raw.author?.let { Author(it) }

    class Author(raw: EmbedUserResponse) : IEmbed.IAuthor {
        override val name: String? = raw.name
        override val url: String? = raw.url
        override val iconUrl: String? = raw.icon_url
    }

    @ExperimentalCoroutinesApi
    override val fields: Flow<IEmbed.IField> = (raw.fields ?: emptyArray()).map { Field(it) }.asFlow()

    class Field(raw: FieldResponse) : IEmbed.IField {
        override val name: String = raw.name
        override val value: String = raw.value
        override val inline: Boolean? = raw.inline
    }

    override val client: IDiscordClient by instance()
}