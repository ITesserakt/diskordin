package org.tesserakt.diskordin.core.data.json.request

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.OverwriteResponse


data class ChannelEditRequest(
    val name: String? = null,
    val position: Int? = null,
    val topic: String? = null,
    val nsfw: Boolean? = null,
    val rate_limit_per_user: Long? = null,
    val bitrate: Int? = null,
    val user_limit: Int? = null,
    val permission_overwrites: Array<OverwriteResponse>? = null,
    val parent_id: Snowflake? = null
) : JsonRequest() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChannelEditRequest

        if (name != other.name) return false
        if (position != other.position) return false
        if (topic != other.topic) return false
        if (nsfw != other.nsfw) return false
        if (rate_limit_per_user != other.rate_limit_per_user) return false
        if (bitrate != other.bitrate) return false
        if (user_limit != other.user_limit) return false
        if (permission_overwrites != null) {
            if (other.permission_overwrites == null) return false
            if (!permission_overwrites.contentEquals(other.permission_overwrites)) return false
        } else if (other.permission_overwrites != null) return false
        if (parent_id != other.parent_id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (position ?: 0)
        result = 31 * result + (topic?.hashCode() ?: 0)
        result = 31 * result + (nsfw?.hashCode() ?: 0)
        result = 31 * result + (rate_limit_per_user?.hashCode() ?: 0)
        result = 31 * result + (bitrate ?: 0)
        result = 31 * result + (user_limit ?: 0)
        result = 31 * result + (permission_overwrites?.contentHashCode() ?: 0)
        result = 31 * result + (parent_id?.hashCode() ?: 0)
        return result
    }
}
