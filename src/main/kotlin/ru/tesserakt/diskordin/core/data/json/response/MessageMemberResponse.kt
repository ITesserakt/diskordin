package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.IMember
import ru.tesserakt.diskordin.impl.core.entity.MessageMember
import java.time.Instant


data class MessageMemberResponse(
    val roles: Array<Long>,
    val nick: String? = null,
    val mute: Boolean,
    val deaf: Boolean,
    val joined_at: Instant
) : DiscordResponse<IMember>() {
    override fun unwrap(vararg params: Any): IMember = MessageMember(this, params[0] as Snowflake)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MessageMemberResponse

        if (!roles.contentEquals(other.roles)) return false
        if (nick != other.nick) return false
        if (mute != other.mute) return false
        if (deaf != other.deaf) return false
        if (joined_at != other.joined_at) return false

        return true
    }

    override fun hashCode(): Int {
        var result = roles.contentHashCode()
        result = 31 * result + (nick?.hashCode() ?: 0)
        result = 31 * result + mute.hashCode()
        result = 31 * result + deaf.hashCode()
        result = 31 * result + joined_at.hashCode()
        return result
    }
}
