package org.tesserakt.diskordin.core.data.json.response

import kotlinx.datetime.Instant
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.`object`.IInvite

data class InviteResponse<out I : IInvite>(
    val code: String,
    val guild: GuildResponse? = null,
    val channel: ChannelResponse<*>? = null,
    val inviter: UserResponse<IUser>? = null,
    val target_user: UserResponse<IUser>? = null,
    val target_user_type: Int? = null,
    val approximate_presence_count: Int? = null,
    val approximate_member_count: Int? = null,
    val uses: Int? = null,
    val maxUses: Int? = null,
    val maxAge: Int? = null,
    val temporary: Boolean? = null,
    val createdAt: Instant? = null
) : DiscordResponse<I, UnwrapContext.EmptyContext>() {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): I = IInvite.typed(this)
}