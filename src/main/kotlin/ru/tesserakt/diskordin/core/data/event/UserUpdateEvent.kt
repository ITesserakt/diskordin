package ru.tesserakt.diskordin.core.data.event

import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.UserResponse
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.util.combine

class UserUpdateEvent(raw: UserResponse<IUser>) : IEvent {
    val user = raw.id.asSnowflake() combine { raw.unwrap() }
}
