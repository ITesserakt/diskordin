package org.tesserakt.diskordin.core.data.json.request


data class MemberAddRequest(
    val access_token: String,
    val nick: String? = null,
    val roles: Array<Long>? = null,
    val mute: Boolean? = null,
    val deaf: Boolean? = null
) : JsonRequest() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MemberAddRequest

        if (access_token != other.access_token) return false
        if (nick != other.nick) return false
        if (roles != null) {
            if (other.roles == null) return false
            if (!roles.contentEquals(other.roles)) return false
        } else if (other.roles != null) return false
        if (mute != other.mute) return false
        if (deaf != other.deaf) return false

        return true
    }

    override fun hashCode(): Int {
        var result = access_token.hashCode()
        result = 31 * result + (nick?.hashCode() ?: 0)
        result = 31 * result + (roles?.contentHashCode() ?: 0)
        result = 31 * result + (mute?.hashCode() ?: 0)
        result = 31 * result + (deaf?.hashCode() ?: 0)
        return result
    }
}
