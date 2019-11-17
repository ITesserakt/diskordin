package org.tesserakt.diskordin.gateway.json.events

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.gateway.json.IRawEvent

data class MessageDelete(
    val id: Snowflake,
    val channelId: Snowflake,
    val guildId: Snowflake? = null
) : IRawEvent
