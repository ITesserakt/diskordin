package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IEmoji
import org.tesserakt.diskordin.core.entity.IUser


data class EmojiResponse<out E : IEmoji>(
    val id: Snowflake?,
    val name: String,
    val roles: List<Snowflake>? = null,
    val user: UserResponse<IUser>? = null,
    val require_colons: Boolean? = null,
    val managed: Boolean? = null,
    val animated: Boolean? = null
) : DiscordResponse<E, UnwrapContext.PartialGuildContext>() {
    override fun unwrap(ctx: UnwrapContext.PartialGuildContext): E = IEmoji.typed(this, ctx.guildId)
}
