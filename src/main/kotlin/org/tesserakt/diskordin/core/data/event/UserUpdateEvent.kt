package org.tesserakt.diskordin.core.data.event

import arrow.core.ForId
import org.tesserakt.diskordin.core.data.identifyId
import org.tesserakt.diskordin.core.data.json.response.UserResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IUser

class UserUpdateEvent(raw: UserResponse<IUser>) : IUserEvent<ForId> {
    override val user = raw.id.identifyId { raw.unwrap() }
}
