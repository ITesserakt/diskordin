package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.impl.core.entity.Webhook


data class WebhookResponse(
    val id: Long,
    val guild_id: Long? = null,
    val channel_id: Long,
    val user: UserResponse? = null,
    val name: String?,
    val avatar: String?,
    val token: String
) : DiscordResponse() {
    fun unwrap() = Webhook(this)
}
