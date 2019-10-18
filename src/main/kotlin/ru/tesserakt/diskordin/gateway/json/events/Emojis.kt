package ru.tesserakt.diskordin.gateway.json.events

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.response.EmojiResponse
import ru.tesserakt.diskordin.core.entity.ICustomEmoji
import ru.tesserakt.diskordin.gateway.json.IRawEvent

data class Emojis(
    val guildId: Snowflake,
    val emojis: Array<EmojiResponse<ICustomEmoji>>
) : IRawEvent {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Emojis) return false

        if (guildId != other.guildId) return false
        if (!emojis.contentEquals(other.emojis)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = guildId.hashCode()
        result = 31 * result + emojis.contentHashCode()
        return result
    }
}