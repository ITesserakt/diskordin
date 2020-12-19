package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.`object`.IGuildEmbed
import org.tesserakt.diskordin.impl.core.entity.`object`.GuildEmbed


data class GuildEmbedResponse(
    val enabled: Boolean,
    val channel_id: Snowflake?
) : DiscordResponse<IGuildEmbed, UnwrapContext.EmptyContext>() {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): IGuildEmbed = GuildEmbed(this)
}
