package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.json.request.EmbedCreateRequest
import java.awt.Color
import java.time.Instant

class EmbedCreateBuilder : IBuilder<EmbedCreateRequest> {
    var title: String? = null
    var description: String? = null
    var url: String? = null
    var timestamp: Instant? = null
    var color: Color? = null
    var footer: (FooterBuilder.() -> Unit)? = null
    var image: (ImageBuilder.() -> Unit)? = null
    var thumbnail: (ThumbnailBuilder.() -> Unit)? = null
    var author: (AuthorBuilder.() -> Unit)? = null
    var fields: Array<(FieldBuilder.() -> Unit)>? = null

    class FieldBuilder : IBuilder<EmbedCreateRequest.FieldRequest> {
        lateinit var name: String
        lateinit var value: Any
        var inline: Boolean? = null

        override fun create(): EmbedCreateRequest.FieldRequest = EmbedCreateRequest.FieldRequest(
            name,
            value.toString(),
            inline
        )
    }

    class AuthorBuilder : IBuilder<EmbedCreateRequest.AuthorRequest> {
        var name: String? = null
        var url: String? = null
        var iconUrl: String? = null

        override fun create(): EmbedCreateRequest.AuthorRequest = EmbedCreateRequest.AuthorRequest(
            name,
            url,
            iconUrl
        )
    }

    class ThumbnailBuilder : IBuilder<EmbedCreateRequest.ThumbnailRequest> {
        var url: String? = null

        override fun create(): EmbedCreateRequest.ThumbnailRequest = EmbedCreateRequest.ThumbnailRequest(
            url
        )
    }

    open class ImageBuilder : IBuilder<EmbedCreateRequest.ImageRequest> {
        var url: String? = null

        override fun create(): EmbedCreateRequest.ImageRequest = EmbedCreateRequest.ImageRequest(
            url
        )
    }

    class FooterBuilder : IBuilder<EmbedCreateRequest.FooterRequest> {
        lateinit var text: String
        var url: String? = null

        override fun create(): EmbedCreateRequest.FooterRequest = EmbedCreateRequest.FooterRequest(
            text,
            url
        )
    }

    override fun create(): EmbedCreateRequest = EmbedCreateRequest(
        title,
        description,
        url,
        timestamp?.toString(),
        color?.rgb,
        footer?.let {
            FooterBuilder().apply(it).create()
        },
        image?.let {
            ImageBuilder().apply(it).create()
        },
        thumbnail?.let {
            ThumbnailBuilder().apply(it).create()
        },
        author?.let {
            AuthorBuilder().apply(it).create()
        },
        fields?.map {
            FieldBuilder().apply(it).create()
        }?.toTypedArray()
    )
}