package org.tesserakt.diskordin.gateway.json.events

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.EmojiResponse
import org.tesserakt.diskordin.core.entity.ICustomEmoji
import org.tesserakt.diskordin.gateway.json.IRawEvent

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