package org.tesserakt.diskordin.core.data.event

import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.UserResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IUser

class UserUpdateEvent(raw: UserResponse<IUser>) : IEvent {
    val user = raw.id identify { raw.unwrap() }
}
