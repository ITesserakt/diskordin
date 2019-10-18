package ru.tesserakt.diskordin.gateway.json.events

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.gateway.json.IRawEvent

class AllReactionsRemove(
    val channelId: Snowflake,
    val messageId: Snowflake,
    val guildId: Snowflake?
) : IRawEvent
