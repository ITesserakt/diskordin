package org.tesserakt.diskordin.gateway.json.events

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.gateway.json.IRawEvent

data class Typing(
    val channelId: Snowflake,
    val guildId: Snowflake?,
    val userId: Snowflake,
    val timestamp: Long
) : IRawEvent
