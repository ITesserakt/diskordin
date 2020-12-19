package org.tesserakt.diskordin.gateway.json.events

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.UserResponse
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.gateway.json.IRawEvent

data class Ban(
    val guildId: Snowflake,
    val user: UserResponse<IUser>
) : IRawEvent