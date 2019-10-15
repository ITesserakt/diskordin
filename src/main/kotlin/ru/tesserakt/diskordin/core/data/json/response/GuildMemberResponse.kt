package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.IMember
import ru.tesserakt.diskordin.impl.core.entity.Member


data class GuildMemberResponse(
    val user: UserResponse<IMember>,
    val nick: String? = null,
    val roles: Array<Long>,
    val joinedAt: String,
    val deaf: Boolean,
    val mute: Boolean
) : DiscordResponse<IMember>() {
    override fun unwrap(vararg params: Any): IMember = Member(this, params[0] as Snowflake)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GuildMemberResponse

        if (user != other.user) return false
        if (nick != other.nick) return false
        if (!roles.contentEquals(other.roles)) return false
        if (joinedAt != other.joinedAt) return false
        if (deaf != other.deaf) return false
        if (mute != other.mute) return false

        return true
    }

    override fun hashCode(): Int {
        var result = user.hashCode()
        result = 31 * result + (nick?.hashCode() ?: 0)
        result = 31 * result + roles.contentHashCode()
        result = 31 * result + joinedAt.hashCode()
        result = 31 * result + deaf.hashCode()
        result = 31 * result + mute.hashCode()
        return result
    }
}