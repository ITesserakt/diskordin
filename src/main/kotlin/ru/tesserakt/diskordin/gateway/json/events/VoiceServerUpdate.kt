package ru.tesserakt.diskordin.gateway.json.events

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.gateway.json.IRawEvent

data class VoiceServerUpdate(
    val token: String,
    val guildId: Snowflake,
    val endpoint: String
) : IRawEvent
