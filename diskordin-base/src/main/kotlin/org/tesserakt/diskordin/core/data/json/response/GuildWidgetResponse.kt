package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IGuildWidget
import org.tesserakt.diskordin.impl.core.entity.GuildWidget

data class GuildWidgetResponse(
    val id: Snowflake,
    val name: String,
    val instantInvite: String,
    val channels: List<ChannelResponse>,
    val members: List<MemberResponse>,
    val presenceCount: Int
) : DiscordResponse<IGuildWidget, UnwrapContext.EmptyContext>() {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): IGuildWidget = GuildWidget(this)

    inner class ChannelResponse(
        val id: Snowflake,
        val name: String,
        val position: Int
    ) : DiscordResponse<IGuildWidget.IChannelPreview, UnwrapContext.GuildContext>() {
        override fun unwrap(ctx: UnwrapContext.GuildContext): IGuildWidget.IChannelPreview =
            GuildWidget.ChannelPreview(this, ctx.guildId)
    }

    data class MemberResponse(
        val id: Long,
        val username: String,
        val discriminator: Short,
        val avatar: String?,
        val status: String,
        val avatarUrl: String?
    ) : DiscordResponse<IGuildWidget.IMemberPreview, UnwrapContext.GuildContext>() {
        override fun unwrap(ctx: UnwrapContext.GuildContext): IGuildWidget.IMemberPreview =
            GuildWidget.MemberPreview(this, ctx.guildId)
    }
}