@file:JvmMultifileClass

package org.tesserakt.diskordin.core.data.event.guild

import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.event.IUserEvent
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.IMember
import org.tesserakt.diskordin.core.entity.IRole
import org.tesserakt.diskordin.core.entity.IUser

interface IGuildEvent<F> : IEvent {
    val guild: IdentifiedF<F, IGuild>
}

interface IMemberEvent<F, G> : IGuildEvent<G>, IUserEvent<F> {
    val member: IdentifiedF<F, IMember>

    override val user: IdentifiedF<F, IUser> get() = member
}

interface IRoleEvent<F, G> : IGuildEvent<G> {
    val role: IdentifiedF<F, IRole>
}
