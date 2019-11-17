package org.tesserakt.diskordin.gateway.json.events

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.EmojiResponse
import org.tesserakt.diskordin.core.entity.IEmoji
import org.tesserakt.diskordin.gateway.json.IRawEvent

data class Reaction(
    val userId: Snowflake,
    val channelId: Snowflake,
    val messageId: Snowflake,
    val guildId: Snowflake?,
    val emoji: EmojiResponse<IEmoji>
) : IRawEvent
