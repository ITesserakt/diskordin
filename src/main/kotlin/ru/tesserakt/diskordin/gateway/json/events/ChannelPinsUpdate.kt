package ru.tesserakt.diskordin.gateway.json.events

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.gateway.json.IRawEvent
import java.time.Instant

data class ChannelPinsUpdate(
    val guildId: Snowflake?,
    val channelId: Snowflake,
    val lastPinTimestamp: Instant
) : IRawEvent