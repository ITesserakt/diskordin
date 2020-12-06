package org.tesserakt.diskordin.core.data.json.request


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
    val fields: List<FieldRequest>? = null
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
}
