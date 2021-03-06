package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.ICustomEmoji
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.IGuildChannel
import org.tesserakt.diskordin.impl.core.entity.Guild

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
    val roles: Set<RoleResponse>,
    val emojis: List<EmojiResponse<ICustomEmoji>>,
    val features: List<String>,
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
    val premiumSubscribersCount: Int?,
    val members: Set<GuildMemberResponse> = emptySet(),
    val channels: Set<ChannelResponse<IGuildChannel>> = emptySet()
) : DiscordResponse<IGuild, UnwrapContext.EmptyContext>() {
    @Suppress("UselessCallOnNotNull") // if this unwraps with gson, members or channels may become null
    override fun unwrap(ctx: UnwrapContext.EmptyContext): IGuild = copy(
        channels = channels.orEmpty().map { it.copy(guild_id = it.guild_id ?: id) }.toSet(),
        members = members.orEmpty()
    ).let(::Guild)
}
