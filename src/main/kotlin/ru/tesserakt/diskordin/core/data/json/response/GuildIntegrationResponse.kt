package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.IIntegration
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.impl.core.entity.Integration

data class GuildIntegrationResponse(
    val id: Snowflake,
    val name: String,
    val type: String,
    val enabled: Boolean,
    val syncing: Boolean,
    val role_id: Snowflake,
    val expire_behavior: Int,
    val expire_grace_period: Int,
    val user: UserResponse<IUser>,
    val account: AccountResponse,
    val synced_at: String
) : DiscordResponse<IIntegration, UnwrapContext.GuildContext>() {
    override fun unwrap(ctx: UnwrapContext.GuildContext): IIntegration = Integration(this, ctx.guildId)
}