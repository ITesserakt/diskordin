package org.tesserakt.diskordin.core.data.json.request

import org.tesserakt.diskordin.core.data.Snowflake
import java.io.File

data class EmojiCreateRequest(
    val name: String,
    val image: File, //TODO: заменить на класс Image
    val roles: Array<Snowflake>
) : JsonRequest() {
    init {
        require(image.isFile && image.extension in listOf("jpg", "png", "jpeg", "tiff", "bmp", "gif")) {
            "It`s not an image!"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EmojiCreateRequest

        if (name != other.name) return false
        if (image != other.image) return false
        if (!roles.contentEquals(other.roles)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + roles.contentHashCode()
        return result
    }
}
