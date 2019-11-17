package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.IWebhook
import org.tesserakt.diskordin.impl.core.entity.Webhook


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
