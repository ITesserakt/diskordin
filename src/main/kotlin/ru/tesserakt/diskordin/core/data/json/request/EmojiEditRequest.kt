package ru.tesserakt.diskordin.core.data.json.request

data class EmojiEditRequest(
    val name: String,
    val roles: Array<Snowflake>
) : JsonRequest() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EmojiEditRequest

        if (name != other.name) return false
        if (!roles.contentEquals(other.roles)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + roles.contentHashCode()
        return result
    }
}
