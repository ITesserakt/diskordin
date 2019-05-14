package ru.tesserakt.diskordin.core.data.json.response


data class InviteResponse(
    val code: String,
    val guild: GuildResponse? = null,
    val channel: ChannelResponse,
    val target_user: UserResponse,
    val target_user_type: Int? = null,
    val approximate_presence_count: Int? = null,
    val approximate_member_count: Int? = null
) : DiscordResponse()