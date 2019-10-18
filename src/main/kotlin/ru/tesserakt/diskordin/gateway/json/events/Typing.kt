package ru.tesserakt.diskordin.gateway.json.events

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.gateway.json.IRawEvent

data class Typing(
    val channelId: Snowflake,
    val guildId: Snowflake?,
    val userId: Snowflake,
    val timestamp: Long
) : IRawEvent
