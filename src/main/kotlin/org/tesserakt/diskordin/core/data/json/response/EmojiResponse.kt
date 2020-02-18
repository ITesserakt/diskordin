package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IEmoji
import org.tesserakt.diskordin.core.entity.IUser

data class EmojiResponse<out E : IEmoji>(
    val id: Snowflake?,
    val name: String = "",
    val roles: Array<Snowflake>? = null,
    val user: UserResponse<IUser>? = null,
    val require_colons: Boolean = false,
    val managed: Boolean = false,
    val animated: Boolean = false
) : DiscordResponse<E, UnwrapContext.PartialGuildContext>() {
    override fun unwrap(ctx: UnwrapContext.PartialGuildContext): E = IEmoji.typed(this, ctx.guildId)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EmojiResponse<*>

        if (id != other.id) return false
        if (name != other.name) return false
        if (roles != null) {
            if (other.roles == null) return false
            if (!roles.contentEquals(other.roles)) return false
        } else if (other.roles != null) return false
        if (user != other.user) return false
        if (require_colons != other.require_colons) return false
        if (managed != other.managed) return false
        if (animated != other.animated) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + (roles?.contentHashCode() ?: 0)
        result = 31 * result + (user?.hashCode() ?: 0)
        result = 31 * result + require_colons.hashCode()
        result = 31 * result + managed.hashCode()
        result = 31 * result + animated.hashCode()
        return result
    }
}
