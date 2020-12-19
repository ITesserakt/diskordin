package org.tesserakt.diskordin.gateway.json.events

import kotlinx.datetime.Instant
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.gateway.json.IRawEvent

data class ChannelPinsUpdate(
    val guildId: Snowflake?,
    val channelId: Snowflake,
    val lastPinTimestamp: Instant
) : IRawEvent