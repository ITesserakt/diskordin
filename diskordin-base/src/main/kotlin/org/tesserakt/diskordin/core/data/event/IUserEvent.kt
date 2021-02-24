package org.tesserakt.diskordin.core.data.event

import org.tesserakt.diskordin.core.data.DeferredIdentified
import org.tesserakt.diskordin.core.data.EagerIdentified
import org.tesserakt.diskordin.core.data.IIdentified
import org.tesserakt.diskordin.core.entity.IUser

interface IUserEvent : IEvent {
    val user: IIdentified<IUser>

    interface Eager : IUserEvent {
        override val user: EagerIdentified<IUser>
    }

    interface Deferred : IUserEvent {
        override val user: DeferredIdentified<IUser>
    }
}