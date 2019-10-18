package ru.tesserakt.diskordin.gateway.json.events

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.gateway.json.IRawEvent

data class WebhooksUpdate(
    val guildId: Snowflake,
    val channelId: Snowflake
) : IRawEvent
