package org.tesserakt.diskordin.gateway.json.events

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.gateway.json.IRawEvent

data class RoleDelete(
    val guildId: Snowflake,
    val roleId: Snowflake
) : IRawEvent
