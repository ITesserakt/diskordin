package ru.tesserakt.diskordin.gateway.json.events

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.response.UserResponse
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.gateway.json.IRawEvent

data class Ban(
    val guildId: Snowflake,
    val user: UserResponse<IUser>
) : IRawEvent