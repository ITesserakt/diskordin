@file:Suppress("MemberVisibilityCanBePrivate")

package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.json.request.EmbedCreateRequest
import java.awt.Color
import java.time.Instant

@Suppress("NOTHING_TO_INLINE", "unused")
@RequestBuilder
class EmbedCreateBuilder : BuilderBase<EmbedCreateRequest>() {
    private var title: String? = null
    private var description: String? = null
    private var url: String? = null
    private var timestamp: Instant? = null
    private var color: Color? = null
    private var footer: FooterBuilder? = null
    private var image: ImageBuilder? = null
    private var thumbnail: ThumbnailBuilder? = null
    private var author: AuthorBuilder? = null
    private val fields: MutableList<FieldBuilder> = mutableListOf()

    operator fun Title.unaryPlus() {
        require(this.v.length < 256)
        title = this.v
    }

    operator fun Description.unaryPlus() {
        require(this.v.length < 2048)
        description = this.v
    }

    operator fun URL.unaryPlus() {
        url = this.v
    }

    operator fun Instant.unaryPlus() {
        timestamp = this
    }

    operator fun Color.unaryPlus() {
        color = this
    }

    operator fun FieldBuilder.unaryPlus() {
        this@EmbedCreateBuilder.fields += this
    }

    operator fun FooterBuilder.unaryPlus() {
        this@EmbedCreateBuilder.footer = this
    }

    operator fun ImageBuilder.unaryPlus() {
        this@EmbedCreateBuilder.image = this
    }

    operator fun ThumbnailBuilder.unaryPlus() {
        this@EmbedCreateBuilder.thumbnail = this
    }

    operator fun AuthorBuilder.unaryPlus() {
        this@EmbedCreateBuilder.author = this
    }

    inline fun EmbedCreateBuilder.title(title: String) = Title(title)
    inline fun EmbedCreateBuilder.description(desc: String) = Description(desc)
    inline fun EmbedCreateBuilder.url(url: String) = URL(url)
    inline fun EmbedCreateBuilder.timestamp(time: Instant) = time
    inline fun EmbedCreateBuilder.image(url: String) = ImageBuilder().apply {
        this.url = url
    }

    inline fun EmbedCreateBuilder.thumbnail(url: String) = ThumbnailBuilder().apply {
        this.url = url
    }

    inline fun EmbedCreateBuilder.author(builder: AuthorBuilder.() -> Unit) = AuthorBuilder().apply(builder)
    inline fun EmbedCreateBuilder.footer(text: String, url: String? = null) = FooterBuilder().apply {
        this.text = text
        this.url = url
    }

    inline fun EmbedCreateBuilder.field(name: String, value: Any, isInline: Boolean = false) = FieldBuilder().apply {
        this.name = name
        this.value = value
        inline = isInline
    }

    @RequestBuilder
    class FieldBuilder : BuilderBase<EmbedCreateRequest.FieldRequest>() {
        lateinit var name: String
        lateinit var value: Any
        var inline: Boolean? = null

        override fun create(): EmbedCreateRequest.FieldRequest = EmbedCreateRequest.FieldRequest(
            name.also { require(it.length < 256) },
            value.toString().also { require(it.length < 1024) },
            inline
        )
    }

    @RequestBuilder
    class AuthorBuilder : BuilderBase<EmbedCreateRequest.AuthorRequest>() {
        private var name: String? = null
        private var url: String? = null
        private var iconUrl: String? = null

        operator fun Name.unaryPlus() {
            require(this.v.length < 1024)
            name = this.v
        }

        operator fun URL.unaryPlus() {
            url = this.v
        }

        operator fun IconURL.unaryPlus() {
            iconUrl = this.v
        }

        inline fun AuthorBuilder.name(name: String) = Name(name)
        inline fun AuthorBuilder.url(url: String) = URL(url)
        inline fun AuthorBuilder.iconUrl(url: String) = IconURL(url)

        override fun create(): EmbedCreateRequest.AuthorRequest = EmbedCreateRequest.AuthorRequest(
            name,
            url,
            iconUrl
        )
    }

    @RequestBuilder
    class ThumbnailBuilder : BuilderBase<EmbedCreateRequest.ThumbnailRequest>() {
        var url: String? = null

        override fun create(): EmbedCreateRequest.ThumbnailRequest = EmbedCreateRequest.ThumbnailRequest(
            url
        )
    }

    @RequestBuilder
    class ImageBuilder : BuilderBase<EmbedCreateRequest.ImageRequest>() {
        var url: String? = null

        override fun create(): EmbedCreateRequest.ImageRequest = EmbedCreateRequest.ImageRequest(
            url
        )
    }

    @RequestBuilder
    class FooterBuilder : BuilderBase<EmbedCreateRequest.FooterRequest>() {
        lateinit var text: String
        var url: String? = null

        override fun create(): EmbedCreateRequest.FooterRequest = EmbedCreateRequest.FooterRequest(
            text.also { require(it.length < 2048) },
            url
        )
    }

    override fun create(): EmbedCreateRequest = EmbedCreateRequest(
        title,
        description,
        url,
        timestamp?.toString(),
        color?.rgb?.and(0xFFFFFF),
        footer?.create(),
        image?.create(),
        thumbnail?.create(),
        author?.create(),
        fields.map { it.create() }
    )
}