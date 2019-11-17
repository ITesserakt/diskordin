package org.tesserakt.diskordin.gateway.json.events

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.gateway.json.IRawEvent

data class VoiceServerUpdate(
    val token: String,
    val guildId: Snowflake,
    val endpoint: String
) : IRawEvent
