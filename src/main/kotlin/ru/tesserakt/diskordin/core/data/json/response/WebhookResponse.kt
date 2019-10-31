package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.core.entity.IWebhook
import ru.tesserakt.diskordin.impl.core.entity.Webhook


data class WebhookResponse(
    val id: Snowflake,
    val guild_id: Snowflake? = null,
    val channel_id: Snowflake,
    val user: UserResponse<IUser>? = null,
    val name: String?,
    val avatar: String?,
    val token: String
) : DiscordResponse<IWebhook, UnwrapContext.EmptyContext>() {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): IWebhook = Webhook(this)
}
