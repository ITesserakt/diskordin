package org.tesserakt.diskordin.impl.core.entity.`object`

import org.tesserakt.diskordin.core.data.json.response.*
import org.tesserakt.diskordin.core.entity.`object`.IEmbed
import org.tesserakt.diskordin.core.entity.`object`.IImage
import org.tesserakt.diskordin.core.entity.`object`.IVideo
import java.awt.Color

internal class Embed(raw: EmbedResponse) : IEmbed {
    override val title: String? = raw.title

    override val type: String? = raw.type

    override val description: String? = raw.description

    override val url: String? = raw.url

    override val timestamp = raw.timestamp

    override val color: Color = Color(raw.color ?: 0)

    override val footer: IEmbed.IFooter? = raw.footer?.unwrap()

    class Footer(raw: FooterResponse) : IEmbed.IFooter {
        override val text: String = raw.text
        override val iconUrl: String? = raw.icon_url

        override fun toString(): String {
            return "Footer(text='$text', iconUrl=$iconUrl)"
        }
    }

    override val image: IImage? = raw.image?.unwrap()

    override val thumbnail: IImage? = raw.thumbnail?.unwrap()

    override val video: IVideo? = raw.video?.unwrap()

    override val provider: IEmbed.IProvider? = raw.provider?.unwrap()

    class Provider(raw: ProviderResponse) : IEmbed.IProvider {
        override val name: String? = raw.name
        override val url: String? = raw.url

        override fun toString(): String {
            return "Provider(name=$name, url=$url)"
        }
    }

    override val author: IEmbed.IAuthor? = raw.author?.unwrap()

    class Author(raw: EmbedUserResponse) : IEmbed.IAuthor {
        override val name: String? = raw.name
        override val url: String? = raw.url
        override val iconUrl: String? = raw.icon_url

        override fun toString(): String {
            return "Author(name=$name, url=$url, iconUrl=$iconUrl)"
        }
    }

    override val fields: List<IEmbed.IField> = (raw.fields ?: emptyArray()).map { it.unwrap() }

    class Field(raw: FieldResponse) : IEmbed.IField {
        override val name: String = raw.name
        override val value: String = raw.value
        override val inline: Boolean? = raw.inline

        override fun toString(): String {
            return "Field(name='$name', value='$value', inline=$inline)"
        }
    }

    override fun toString(): String {
        return "Embed(title=$title, type=$type, description=$description, url=$url, timestamp=$timestamp, color=$color, footer=$footer, image=$image, thumbnail=$thumbnail, video=$video, provider=$provider, author=$author, fields=$fields)"
    }
}