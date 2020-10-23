package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.`object`.ITemplate
import org.tesserakt.diskordin.impl.core.entity.`object`.Template
import java.time.Instant

data class TemplateResponse(
    val code: String,
    val name: String,
    val description: String?,
    val usageCount: Int,
    val creatorId: Snowflake,
    val creator: UserResponse<IUser>,
    val createdAt: Instant,
    val updatedAt: Instant,
    val sourceGuildId: Snowflake,
    val serializedSourceGuild: GuildResponse,
    val isDirty: Boolean?
) : DiscordResponse<ITemplate, UnwrapContext.EmptyContext>() {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): ITemplate = Template(this)
}