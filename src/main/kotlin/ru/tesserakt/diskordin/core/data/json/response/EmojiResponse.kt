package ru.tesserakt.diskordin.core.data.json.response


data class EmojiResponse(
    val id: Long?,
    val name: String,
    val roles: Array<Long>? = null,
    val user: UserResponse? = null,
    val require_colons: Boolean? = null,
    val managed: Boolean? = null,
    val animated: Boolean? = null
) : DiscordResponse() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EmojiResponse

        if (id != other.id) return false
        if (name != other.name) return false
        if (roles != null) {
            if (other.roles == null) return false
            if (!roles.contentEquals(other.roles)) return false
        } else if (other.roles != null) return false
        if (user != other.user) return false
        if (require_colons != other.require_colons) return false
        if (managed != other.managed) return false
        if (animated != other.animated) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + (roles?.contentHashCode() ?: 0)
        result = 31 * result + (user?.hashCode() ?: 0)
        result = 31 * result + (require_colons?.hashCode() ?: 0)
        result = 31 * result + (managed?.hashCode() ?: 0)
        result = 31 * result + (animated?.hashCode() ?: 0)
        return result
    }
}