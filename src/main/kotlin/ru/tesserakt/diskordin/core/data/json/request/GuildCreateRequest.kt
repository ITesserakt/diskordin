package ru.tesserakt.diskordin.core.data.json.request


data class GuildCreateRequest(
    val name: String,
    val region: String,
    val icon: String,
    val verification_level: Int,
    val default_message_notifications: Int,
    val explicit_content_filter: Int,
    val roles: Array<GuildRoleCreateRequest>,
    val channels: Array<PartialChannelCreateRequest>
) : JsonRequest() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GuildCreateRequest

        if (name != other.name) return false
        if (region != other.region) return false
        if (icon != other.icon) return false
        if (verification_level != other.verification_level) return false
        if (default_message_notifications != other.default_message_notifications) return false
        if (explicit_content_filter != other.explicit_content_filter) return false
        if (!roles.contentEquals(other.roles)) return false
        if (!channels.contentEquals(other.channels)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + region.hashCode()
        result = 31 * result + icon.hashCode()
        result = 31 * result + verification_level
        result = 31 * result + default_message_notifications
        result = 31 * result + explicit_content_filter
        result = 31 * result + roles.contentHashCode()
        result = 31 * result + channels.contentHashCode()
        return result
    }
}
