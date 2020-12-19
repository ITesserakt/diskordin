package org.tesserakt.diskordin.core.data.event

import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.entity.IUser

interface IUserEvent<F> : IEvent {
    val user: IdentifiedF<F, IUser>
}