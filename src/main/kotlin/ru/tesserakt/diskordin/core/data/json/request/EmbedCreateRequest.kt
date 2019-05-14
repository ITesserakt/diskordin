package ru.tesserakt.diskordin.core.data.json.request


data class EmbedCreateRequest(
    val title: String? = null,
    val description: String? = null,
    val url: String? = null,
    val timestamp: String? = null,
    val color: Int? = null,
    val footer: FooterRequest? = null,
    val image: ImageRequest? = null,
    val thumbnail: ThumbnailRequest? = null,
    val author: AuthorRequest? = null,
    val fields: Array<FieldRequest>? = null
) : JsonRequest() {
    data class FooterRequest(
        val text: String,
        val iconUrl: String? = null
    ) : JsonRequest()

    data class ImageRequest(
        val url: String? = null
    ) : JsonRequest()

    data class ThumbnailRequest(
        val url: String? = null
    ) : JsonRequest()

    data class AuthorRequest(
        val name: String? = null,
        val url: String? = null,
        val iconUrl: String? = null
    ) : JsonRequest()

    data class FieldRequest(
        val name: String,
        val value: String,
        val inline: Boolean? = null
    ) : JsonRequest()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EmbedCreateRequest

        if (title != other.title) return false
        if (description != other.description) return false
        if (url != other.url) return false
        if (timestamp != other.timestamp) return false
        if (color != other.color) return false
        if (footer != other.footer) return false
        if (image != other.image) return false
        if (thumbnail != other.thumbnail) return false
        if (author != other.author) return false
        if (fields != null) {
            if (other.fields == null) return false
            if (!fields.contentEquals(other.fields)) return false
        } else if (other.fields != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title?.hashCode() ?: 0
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (url?.hashCode() ?: 0)
        result = 31 * result + (timestamp?.hashCode() ?: 0)
        result = 31 * result + (color ?: 0)
        result = 31 * result + (footer?.hashCode() ?: 0)
        result = 31 * result + (image?.hashCode() ?: 0)
        result = 31 * result + (thumbnail?.hashCode() ?: 0)
        result = 31 * result + (author?.hashCode() ?: 0)
        result = 31 * result + (fields?.contentHashCode() ?: 0)
        return result
    }
}
