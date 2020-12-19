package org.tesserakt.diskordin.core.data.json.response

import kotlinx.datetime.Instant
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IIntegration
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.impl.core.entity.Integration

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
    val synced_at: Instant
) : DiscordResponse<IIntegration, UnwrapContext.GuildContext>() {
    override fun unwrap(ctx: UnwrapContext.GuildContext): IIntegration = Integration(this, ctx.guildId)
}