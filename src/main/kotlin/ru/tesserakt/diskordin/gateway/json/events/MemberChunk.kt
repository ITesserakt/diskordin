package ru.tesserakt.diskordin.gateway.json.events

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.response.GuildMemberResponse
import ru.tesserakt.diskordin.gateway.json.IRawEvent

data class MemberChunk(
    val guildId: Snowflake,
    val members: Array<GuildMemberResponse>,
    val notFound: Array<Snowflake>? = null
) : IRawEvent {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MemberChunk) return false

        if (guildId != other.guildId) return false
        if (!members.contentEquals(other.members)) return false
        if (notFound != null) {
            if (other.notFound == null) return false
            if (!notFound.contentEquals(other.notFound)) return false
        } else if (other.notFound != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = guildId.hashCode()
        result = 31 * result + members.contentHashCode()
        result = 31 * result + (notFound?.contentHashCode() ?: 0)
        return result
    }
}
