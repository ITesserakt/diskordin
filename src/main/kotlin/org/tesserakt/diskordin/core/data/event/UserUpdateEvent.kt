package org.tesserakt.diskordin.core.data.event

import arrow.core.extensions.id.applicative.just
import arrow.core.extensions.id.comonad.extract
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.UserResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.rest.storage.GlobalEntityCache

class UserUpdateEvent(raw: UserResponse<IUser>) : IEvent {
    val user = raw.id identify { raw.unwrap().just() }

    init {
        GlobalEntityCache[user.id] = user().extract()
    }
}
