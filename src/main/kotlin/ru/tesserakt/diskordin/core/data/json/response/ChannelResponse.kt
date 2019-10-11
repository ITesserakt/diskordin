package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.core.entity.IChannel


data class ChannelResponse(
    val id: Long,
    val type: Int,
    val guild_id: Long? = null,
    val position: Int? = null,
    val permission_overwrites: Array<OverwriteResponse>? = null,
    val name: String? = null,
    val topic: String? = null,
    val nsfw: Boolean? = null,
    val last_message_id: Long? = null,
    val bitrate: Int? = null,
    val user_limit: Int? = null,
    val rate_limit_per_user: Int? = null,
    val recipients: Array<UserResponse>? = null,
    val icon: String? = null,
    val owner_id: Long? = null,
    val application_id: Long? = null,
    val parent_id: Long? = null,
    val last_pin_timestamp: String? = null
) : DiscordResponse() {
    fun <T : IChannel> unwrap() = IChannel.typed<T>(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChannelResponse

        if (id != other.id) return false
        if (type != other.type) return false
        if (guild_id != other.guild_id) return false
        if (position != other.position) return false
        if (permission_overwrites != null) {
            if (other.permission_overwrites == null) return false
            if (!permission_overwrites.contentEquals(other.permission_overwrites)) return false
        } else if (other.permission_overwrites != null) return false
        if (name != other.name) return false
        if (topic != other.topic) return false
        if (nsfw != other.nsfw) return false
        if (last_message_id != other.last_message_id) return false
        if (bitrate != other.bitrate) return false
        if (user_limit != other.user_limit) return false
        if (rate_limit_per_user != other.rate_limit_per_user) return false
        if (recipients != null) {
            if (other.recipients == null) return false
            if (!recipients.contentEquals(other.recipients)) return false
        } else if (other.recipients != null) return false
        if (icon != other.icon) return false
        if (owner_id != other.owner_id) return false
        if (application_id != other.application_id) return false
        if (parent_id != other.parent_id) return false
        if (last_pin_timestamp != other.last_pin_timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + type
        result = 31 * result + (guild_id?.hashCode() ?: 0)
        result = 31 * result + (position ?: 0)
        result = 31 * result + (permission_overwrites?.contentHashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (topic?.hashCode() ?: 0)
        result = 31 * result + (nsfw?.hashCode() ?: 0)
        result = 31 * result + (last_message_id?.hashCode() ?: 0)
        result = 31 * result + (bitrate ?: 0)
        result = 31 * result + (user_limit ?: 0)
        result = 31 * result + (rate_limit_per_user ?: 0)
        result = 31 * result + (recipients?.contentHashCode() ?: 0)
        result = 31 * result + (icon?.hashCode() ?: 0)
        result = 31 * result + (owner_id?.hashCode() ?: 0)
        result = 31 * result + (application_id?.hashCode() ?: 0)
        result = 31 * result + (parent_id?.hashCode() ?: 0)
        result = 31 * result + (last_pin_timestamp?.hashCode() ?: 0)
        return result
    }
}