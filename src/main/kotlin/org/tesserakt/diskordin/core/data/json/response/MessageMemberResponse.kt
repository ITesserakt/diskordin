package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IMember
import java.time.Instant

data class MessageMemberResponse(
    val roles: List<Snowflake>,
    val nick: String? = null,
    val mute: Boolean,
    val deaf: Boolean,
    val joined_at: Instant
) : DiscordResponse<IMember, UnwrapContext.GuildContext>() {
    override fun unwrap(ctx: UnwrapContext.GuildContext): IMember = TODO("Dead code")
}
