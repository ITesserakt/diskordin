@file:Suppress("MemberVisibilityCanBePrivate")

package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.json.request.EmbedCreateRequest
import java.awt.Color
import java.time.Instant

class EmbedCreateBuilder : BuilderBase<EmbedCreateRequest>() {
    var title: String? = null
        set(value) {
            if (value != null)
                require(value.length < 256)
            field = value
        }
    var description: String? = null
        set(value) {
            if (value != null)
                require(value.length < 2048)
            field = value
        }
    var url: String? = null
    var timestamp: Instant? = null
    var color: Color? = null
    var footer: (FooterBuilder.() -> Unit)? = null
    var image: (ImageBuilder.() -> Unit)? = null
    var thumbnail: (ThumbnailBuilder.() -> Unit)? = null
    var author: (AuthorBuilder.() -> Unit)? = null
    var fields: Array<FieldBuilder.() -> Unit>? = null
        set(value) {
            if (value != null)
                require(value.size < 25)
            field = value
        }

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

    class AuthorBuilder : BuilderBase<EmbedCreateRequest.AuthorRequest>() {
        var name: String? = null
            set(value) {
                if (value != null)
                    require(value.length < 1024)
                field = value
            }
        var url: String? = null
        var iconUrl: String? = null

        override fun create(): EmbedCreateRequest.AuthorRequest = EmbedCreateRequest.AuthorRequest(
            name,
            url,
            iconUrl
        )
    }

    class ThumbnailBuilder : BuilderBase<EmbedCreateRequest.ThumbnailRequest>() {
        var url: String? = null

        override fun create(): EmbedCreateRequest.ThumbnailRequest = EmbedCreateRequest.ThumbnailRequest(
            url
        )
    }

    open class ImageBuilder : BuilderBase<EmbedCreateRequest.ImageRequest>() {
        var url: String? = null

        override fun create(): EmbedCreateRequest.ImageRequest = EmbedCreateRequest.ImageRequest(
            url
        )
    }

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