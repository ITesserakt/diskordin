package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.core.entity.`object`.IInvite


data class InviteResponse<out I : IInvite>(
    val code: String,
    val guild: GuildResponse? = null,
    val channel: ChannelResponse<*>,
    val target_user: UserResponse<IUser>,
    val target_user_type: Int? = null,
    val approximate_presence_count: Int? = null,
    val approximate_member_count: Int? = null
) : DiscordResponse<I>() {
    override fun unwrap(vararg params: Any): I = IInvite.typed(this)
}