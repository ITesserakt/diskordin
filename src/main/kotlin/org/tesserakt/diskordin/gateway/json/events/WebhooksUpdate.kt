package org.tesserakt.diskordin.gateway.json.events

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.gateway.json.IRawEvent

data class WebhooksUpdate(
    val guildId: Snowflake,
    val channelId: Snowflake
) : IRawEvent
