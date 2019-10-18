package ru.tesserakt.diskordin.gateway.json.events

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.gateway.json.IRawEvent

data class MessageBulkDelete(
    val ids: Array<Snowflake>,
    val channelId: Snowflake,
    val guildId: Snowflake? = null
) : IRawEvent {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MessageBulkDelete) return false

        if (!ids.contentEquals(other.ids)) return false
        if (channelId != other.channelId) return false
        if (guildId != other.guildId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ids.contentHashCode()
        result = 31 * result + channelId.hashCode()
        result = 31 * result + (guildId?.hashCode() ?: 0)
        return result
    }
}
