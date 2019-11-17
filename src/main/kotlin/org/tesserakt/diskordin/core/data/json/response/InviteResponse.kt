package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.`object`.IInvite


data class InviteResponse<out I : IInvite>(
    val code: String,
    val guild: GuildResponse? = null,
    val channel: ChannelResponse<*>,
    val target_user: UserResponse<IUser>,
    val target_user_type: Int? = null,
    val approximate_presence_count: Int? = null,
    val approximate_member_count: Int? = null
) : DiscordResponse<I, UnwrapContext.EmptyContext>() {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): I = IInvite.typed(this)
}