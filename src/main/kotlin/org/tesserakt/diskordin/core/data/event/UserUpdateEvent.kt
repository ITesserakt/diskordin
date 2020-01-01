package org.tesserakt.diskordin.core.data.event

import arrow.core.ForId
import arrow.core.extensions.id.applicative.just
import arrow.core.extensions.id.comonad.extract
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.UserResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.cache

class UserUpdateEvent(raw: UserResponse<IUser>) : IUserEvent<ForId> {
    override val user = raw.id identify { raw.unwrap().just() }

    init {
        cache[user.id] = user().extract()
    }
}
