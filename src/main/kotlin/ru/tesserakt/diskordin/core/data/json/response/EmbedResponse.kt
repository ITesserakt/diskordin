@file:Suppress("DuplicatedCode")

package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.core.entity.`object`.IEmbed
import ru.tesserakt.diskordin.core.entity.`object`.IImage
import ru.tesserakt.diskordin.core.entity.`object`.IVideo
import ru.tesserakt.diskordin.impl.core.entity.`object`.Embed
import ru.tesserakt.diskordin.impl.core.entity.`object`.Image
import ru.tesserakt.diskordin.impl.core.entity.`object`.Thumbnail
import ru.tesserakt.diskordin.impl.core.entity.`object`.Video


data class EmbedResponse(
    val title: String? = null,
    val type: String? = null,
    val description: String? = null,
    val url: String? = null,
    val timestamp: String? = null,
    val color: Int? = null,
    val footer: FooterResponse? = null,
    val image: ImageResponse? = null,
    val thumbnail: ThumbnailResponse? = null,
    val video: VideoResponse? = null,
    val provider: ProviderResponse? = null,
    val author: EmbedUserResponse? = null,
    val fields: Array<FieldResponse>? = null
) : DiscordResponse<IEmbed>() {
    override fun unwrap(vararg params: Any): IEmbed = Embed(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EmbedResponse

        if (title != other.title) return false
        if (type != other.type) return false
        if (description != other.description) return false
        if (url != other.url) return false
        if (timestamp != other.timestamp) return false
        if (color != other.color) return false
        if (footer != other.footer) return false
        if (image != other.image) return false
        if (thumbnail != other.thumbnail) return false
        if (video != other.video) return false
        if (provider != other.provider) return false
        if (author != other.author) return false
        if (fields != null) {
            if (other.fields == null) return false
            if (!fields.contentEquals(other.fields)) return false
        } else if (other.fields != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title?.hashCode() ?: 0
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (url?.hashCode() ?: 0)
        result = 31 * result + (timestamp?.hashCode() ?: 0)
        result = 31 * result + (color ?: 0)
        result = 31 * result + (footer?.hashCode() ?: 0)
        result = 31 * result + (image?.hashCode() ?: 0)
        result = 31 * result + (thumbnail?.hashCode() ?: 0)
        result = 31 * result + (video?.hashCode() ?: 0)
        result = 31 * result + (provider?.hashCode() ?: 0)
        result = 31 * result + (author?.hashCode() ?: 0)
        result = 31 * result + (fields?.contentHashCode() ?: 0)
        return result
    }
}


data class FieldResponse(
    val name: String,
    val value: String,
    val inline: Boolean? = null
) : DiscordResponse<IEmbed.IField>() {
    override fun unwrap(vararg params: Any): IEmbed.IField = Embed.Field(this)
}


data class EmbedUserResponse(
    val name: String? = null,
    val url: String? = null,
    val icon_url: String? = null,
    val proxy_icon_url: String? = null
) : DiscordResponse<IEmbed.IAuthor>() {
    override fun unwrap(vararg params: Any): IEmbed.IAuthor = Embed.Author(this)
}


data class ProviderResponse(
    val name: String? = null,
    val url: String? = null
) : DiscordResponse<IEmbed.IProvider>() {
    override fun unwrap(vararg params: Any): IEmbed.IProvider = Embed.Provider(this)
}


data class VideoResponse(
    val url: String? = null,
    val height: Int? = null,
    val width: Int? = null
) : DiscordResponse<IVideo>() {
    override fun unwrap(vararg params: Any): IVideo = Video(this)
}


data class ThumbnailResponse(
    val url: String? = null,
    val proxy_url: String? = null,
    val height: Int? = null,
    val width: Int? = null
) : DiscordResponse<IImage>() {
    override fun unwrap(vararg params: Any): IImage = Thumbnail(this)
}


data class ImageResponse(
    val url: String? = null,
    val proxy_url: String?,
    val height: Int? = null,
    val width: Int? = null
) : DiscordResponse<IImage>() {
    override fun unwrap(vararg params: Any): IImage = Image(this)
}


data class FooterResponse(
    val text: String,
    val icon_url: String? = null,
    val proxy_icon_url: String? = null
) : DiscordResponse<IEmbed.IFooter>() {
    override fun unwrap(vararg params: Any): IEmbed.IFooter = Embed.Footer(this)
}
