package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.ICustomEmoji
import ru.tesserakt.diskordin.core.entity.IGuild
import ru.tesserakt.diskordin.impl.core.entity.Guild

data class GuildResponse(
    val id: Snowflake,
    val name: String,
    val icon: String?,
    val splash: String?,
    val owner: Boolean? = null,
    val owner_id: Snowflake,
    val permissions: Int? = null,
    val region: String,
    val afk_channel_id: Snowflake?,
    val afk_timeout: Int,
    val embed_enabled: Boolean? = null,
    val embed_channel_id: Snowflake? = null,
    val verification_level: Int,
    val default_message_notifications: Int,
    val explicit_content_filter: Int,
    val roles: Array<RoleResponse>,
    val emojis: Array<EmojiResponse<ICustomEmoji>>,
    val features: Array<String>,
    val mfa_level: Int,
    val application_id: Snowflake?,
    val widget_enabled: Boolean? = null,
    val system_channel_id: Snowflake?,
    val max_presences: Long?,
    val max_members: Long?,
    val vanity_url_code: String?,
    val description: String?,
    val banner: String?,
    val widget_channel_id: Snowflake?,
    val system_channel_flags: Long? = null,
    val premium_tier: Int? = null,
    val premiumSubscribersCount: Int?
) : DiscordResponse<IGuild>() {

    override fun unwrap(vararg params: Any): IGuild = Guild(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GuildResponse

        if (id != other.id) return false
        if (name != other.name) return false
        if (icon != other.icon) return false
        if (splash != other.splash) return false
        if (owner != other.owner) return false
        if (owner_id != other.owner_id) return false
        if (permissions != other.permissions) return false
        if (region != other.region) return false
        if (afk_channel_id != other.afk_channel_id) return false
        if (afk_timeout != other.afk_timeout) return false
        if (embed_enabled != other.embed_enabled) return false
        if (embed_channel_id != other.embed_channel_id) return false
        if (verification_level != other.verification_level) return false
        if (default_message_notifications != other.default_message_notifications) return false
        if (explicit_content_filter != other.explicit_content_filter) return false
        if (!roles.contentEquals(other.roles)) return false
        if (!emojis.contentEquals(other.emojis)) return false
        if (!features.contentEquals(other.features)) return false
        if (mfa_level != other.mfa_level) return false
        if (application_id != other.application_id) return false
        if (widget_enabled != other.widget_enabled) return false
        if (system_channel_id != other.system_channel_id) return false
        if (max_presences != other.max_presences) return false
        if (max_members != other.max_members) return false
        if (vanity_url_code != other.vanity_url_code) return false
        if (description != other.description) return false
        if (banner != other.banner) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (icon?.hashCode() ?: 0)
        result = 31 * result + (splash?.hashCode() ?: 0)
        result = 31 * result + (owner?.hashCode() ?: 0)
        result = 31 * result + owner_id.hashCode()
        result = 31 * result + (permissions ?: 0)
        result = 31 * result + region.hashCode()
        result = 31 * result + (afk_channel_id?.hashCode() ?: 0)
        result = 31 * result + afk_timeout
        result = 31 * result + (embed_enabled?.hashCode() ?: 0)
        result = 31 * result + (embed_channel_id?.hashCode() ?: 0)
        result = 31 * result + verification_level
        result = 31 * result + default_message_notifications
        result = 31 * result + explicit_content_filter
        result = 31 * result + roles.contentHashCode()
        result = 31 * result + emojis.contentHashCode()
        result = 31 * result + features.contentHashCode()
        result = 31 * result + mfa_level
        result = 31 * result + (application_id?.hashCode() ?: 0)
        result = 31 * result + (widget_enabled?.hashCode() ?: 0)
        result = 31 * result + (system_channel_id?.hashCode() ?: 0)
        result = 31 * result + (max_presences?.hashCode() ?: 0)
        result = 31 * result + max_members.hashCode()
        result = 31 * result + (vanity_url_code?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (banner?.hashCode() ?: 0)
        return result
    }
}
