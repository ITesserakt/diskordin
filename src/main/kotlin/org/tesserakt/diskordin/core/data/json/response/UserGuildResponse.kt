package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.impl.core.entity.PartialGuild

data class UserGuildResponse(
    val id: Snowflake,
    val name: String,
    val icon: String,
    val owner: Boolean,
    val permissions: Int
) : DiscordResponse<IGuild, UnwrapContext.EmptyContext>() {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): IGuild = PartialGuild(this)
}