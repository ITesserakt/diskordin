package ru.tesserakt.diskordin.core.data.json.response


data class MessageUserResponse(
    val username: String,
    val id: Long,
    val discriminator: String,
    val avatar: String?,
    val bot: Boolean? = null,
    val member: MessageMemberResponse? = null
) : DiscordResponse()
