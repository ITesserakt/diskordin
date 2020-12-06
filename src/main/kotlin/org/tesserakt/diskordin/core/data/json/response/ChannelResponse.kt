package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IChannel
import org.tesserakt.diskordin.core.entity.IUser


data class ChannelResponse<out C : IChannel>(
    val id: Snowflake,
    val type: Int,
    val guild_id: Snowflake? = null,
    val position: Int? = null,
    val permission_overwrites: List<OverwriteResponse>? = null,
    val name: String? = null,
    val topic: String? = null,
    val nsfw: Boolean? = null,
    val last_message_id: Snowflake? = null,
    val bitrate: Int? = null,
    val user_limit: Int? = null,
    val rate_limit_per_user: Int? = null,
    val recipients: List<UserResponse<IUser>>? = null,
    val icon: String? = null,
    val owner_id: Snowflake? = null,
    val application_id: Snowflake? = null,
    val parent_id: Snowflake? = null,
    val last_pin_timestamp: String? = null
) : DiscordResponse<C, UnwrapContext.EmptyContext>() {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): C = IChannel.typed(this)
}