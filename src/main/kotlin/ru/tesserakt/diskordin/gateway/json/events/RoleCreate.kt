package ru.tesserakt.diskordin.gateway.json.events

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.response.RoleResponse
import ru.tesserakt.diskordin.gateway.json.IRawEvent

data class RoleCreate(
    val guildId: Snowflake,
    val role: RoleResponse
) : IRawEvent
