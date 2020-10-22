package org.tesserakt.diskordin.core.data.event

import arrow.core.ForId
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.data.identifyId
import org.tesserakt.diskordin.core.data.invoke
import org.tesserakt.diskordin.core.data.json.response.UserResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.cache

class UserUpdateEvent(raw: UserResponse<IUser>) : IUserEvent<ForId> {
    override val user = raw.id.identifyId { raw.unwrap() }

    init {
        cache[user.id] = user()
    }
}
