package ru.tesserakt.diskordin.gateway.json.events

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.gateway.json.IRawEvent

data class MessageDelete(
    val id: Snowflake,
    val channelId: Snowflake,
    val guildId: Snowflake? = null
) : IRawEvent
