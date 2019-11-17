package org.tesserakt.diskordin.gateway.json.events

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.gateway.json.IRawEvent
import java.time.Instant

data class ChannelPinsUpdate(
    val guildId: Snowflake?,
    val channelId: Snowflake,
    val lastPinTimestamp: Instant
) : IRawEvent