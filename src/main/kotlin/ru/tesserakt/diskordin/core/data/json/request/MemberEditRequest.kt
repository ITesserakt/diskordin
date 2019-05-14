package ru.tesserakt.diskordin.core.data.json.request


data class MemberEditRequest(
    val nick: String? = null,
    val roles: Array<Long>? = null,
    val mute: Boolean? = null,
    val deaf: Boolean? = null,
    val channel_id: Long? = null
) : JsonRequest() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MemberEditRequest

        if (nick != other.nick) return false
        if (roles != null) {
            if (other.roles == null) return false
            if (!roles.contentEquals(other.roles)) return false
        } else if (other.roles != null) return false
        if (mute != other.mute) return false
        if (deaf != other.deaf) return false
        if (channel_id != other.channel_id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nick?.hashCode() ?: 0
        result = 31 * result + (roles?.contentHashCode() ?: 0)
        result = 31 * result + (mute?.hashCode() ?: 0)
        result = 31 * result + (deaf?.hashCode() ?: 0)
        result = 31 * result + (channel_id?.hashCode() ?: 0)
        return result
    }
}
