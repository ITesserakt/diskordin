package org.tesserakt.diskordin.gateway.json.events

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.gateway.json.IRawEvent

class AllReactionsRemove(
    val channelId: Snowflake,
    val messageId: Snowflake,
    val guildId: Snowflake?
) : IRawEvent
