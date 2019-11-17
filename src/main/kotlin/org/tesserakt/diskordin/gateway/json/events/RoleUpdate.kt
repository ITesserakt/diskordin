package org.tesserakt.diskordin.gateway.json.events

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.RoleResponse
import org.tesserakt.diskordin.gateway.json.IRawEvent

data class RoleUpdate(
    val guildId: Snowflake,
    val role: RoleResponse
) : IRawEvent
