package org.tesserakt.diskordin.gateway.json.events

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.UserResponse
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.gateway.json.IRawEvent

data class MemberUpdate(
    val guildId: Snowflake,
    val roles: Array<Snowflake>,
    val user: UserResponse<IUser>,
    val nick: String?
) : IRawEvent {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MemberUpdate) return false

        if (guildId != other.guildId) return false
        if (!roles.contentEquals(other.roles)) return false
        if (user != other.user) return false
        if (nick != other.nick) return false

        return true
    }

    override fun hashCode(): Int {
        var result = guildId.hashCode()
        result = 31 * result + roles.contentHashCode()
        result = 31 * result + user.hashCode()
        result = 31 * result + nick.hashCode()
        return result
    }
}
