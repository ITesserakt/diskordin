package ru.tesserakt.diskordin.core.data.json.response


data class WebhookResponse(
    val id: Long,
    val guild_id: Long? = null,
    val channel_id: Long,
    val user: UserResponse? = null,
    val name: String?,
    val avatar: String?,
    val token: String
) : DiscordResponse()
