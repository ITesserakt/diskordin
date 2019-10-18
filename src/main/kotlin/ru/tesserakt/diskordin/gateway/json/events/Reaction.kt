package ru.tesserakt.diskordin.gateway.json.events

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.response.EmojiResponse
import ru.tesserakt.diskordin.core.entity.IEmoji
import ru.tesserakt.diskordin.gateway.json.IRawEvent

data class Reaction(
    val userId: Snowflake,
    val channelId: Snowflake,
    val messageId: Snowflake,
    val guildId: Snowflake?,
    val emoji: EmojiResponse<IEmoji>
) : IRawEvent
