package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.core.entity.`object`.IInvite


data class InviteResponse(
    val code: String,
    val guild: GuildResponse? = null,
    val channel: ChannelResponse,
    val target_user: UserResponse,
    val target_user_type: Int? = null,
    val approximate_presence_count: Int? = null,
    val approximate_member_count: Int? = null
) : DiscordResponse() {
    fun <T : IInvite> unwrap() = IInvite.typed<T>(this)
}