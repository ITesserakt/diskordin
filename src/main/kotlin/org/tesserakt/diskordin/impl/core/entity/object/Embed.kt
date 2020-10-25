package org.tesserakt.diskordin.impl.core.entity.`object`


import org.tesserakt.diskordin.core.data.json.response.*
import org.tesserakt.diskordin.core.entity.`object`.IEmbed
import org.tesserakt.diskordin.core.entity.`object`.IImage
import org.tesserakt.diskordin.core.entity.`object`.IVideo
import java.awt.Color
import java.time.Instant
import java.time.format.DateTimeFormatter

internal class Embed(raw: EmbedResponse) : IEmbed {
    override val title: String? = raw.title

    override val type: String? = raw.type

    override val description: String? = raw.description

    override val url: String? = raw.url

    override val timestamp: Instant? = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(raw.timestamp, Instant::from)

    override val color: Color = Color(raw.color ?: 0)

    override val footer: IEmbed.IFooter? = raw.footer?.unwrap()

    class Footer(raw: FooterResponse) : IEmbed.IFooter {
        override val text: String = raw.text
        override val iconUrl: String? = raw.icon_url
        override fun toString(): String {
            return StringBuilder("Footer(")
                .appendLine("text='$text', ")
                .appendLine("iconUrl=$iconUrl")
                .appendLine(")")
                .toString()
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
            return StringBuilder("Provider(")
                .appendLine("name=$name, ")
                .appendLine("url=$url")
                .appendLine(")")
                .toString()
        }
    }

    override val author: IEmbed.IAuthor? = raw.author?.unwrap()

    class Author(raw: EmbedUserResponse) : IEmbed.IAuthor {
        override val name: String? = raw.name
        override val url: String? = raw.url
        override val iconUrl: String? = raw.icon_url
        override fun toString(): String {
            return StringBuilder("Author(")
                .appendLine("name=$name, ")
                .appendLine("url=$url, ")
                .appendLine("iconUrl=$iconUrl")
                .appendLine(")")
                .toString()
        }
    }

    override val fields: List<IEmbed.IField> = (raw.fields ?: emptyArray()).map { it.unwrap() }

    class Field(raw: FieldResponse) : IEmbed.IField {
        override val name: String = raw.name
        override val value: String = raw.value
        override val inline: Boolean? = raw.inline
        override fun toString(): String {
            return StringBuilder("Field(")
                .appendLine("name='$name', ")
                .appendLine("value='$value', ")
                .appendLine("inline=$inline")
                .appendLine(")")
                .toString()
        }
    }

    override fun toString(): String {
        return StringBuilder("Embed(")
            .appendLine("title=$title, ")
            .appendLine("type=$type, ")
            .appendLine("description=$description, ")
            .appendLine("url=$url, ")
            .appendLine("timestamp=$timestamp, ")
            .appendLine("color=$color, ")
            .appendLine("footer=$footer, ")
            .appendLine("image=$image, ")
            .appendLine("thumbnail=$thumbnail, ")
            .appendLine("video=$video, ")
            .appendLine("provider=$provider, ")
            .appendLine("author=$author, ")
            .appendLine("fields=$fields")
            .appendLine(")")
            .toString()
    }
}