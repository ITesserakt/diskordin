package ru.tesserakt.diskordin.core.data.event

import ru.tesserakt.diskordin.core.data.combine
import ru.tesserakt.diskordin.core.data.json.response.UserResponse
import ru.tesserakt.diskordin.core.data.json.response.unwrap
import ru.tesserakt.diskordin.core.entity.IUser

class UserUpdateEvent(raw: UserResponse<IUser>) : IEvent {
    val user = raw.id combine { raw.unwrap() }
}
