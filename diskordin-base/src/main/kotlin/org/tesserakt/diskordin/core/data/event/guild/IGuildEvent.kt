@file:JvmMultifileClass

package org.tesserakt.diskordin.core.data.event.guild

import org.tesserakt.diskordin.core.data.DeferredIdentified
import org.tesserakt.diskordin.core.data.EagerIdentified
import org.tesserakt.diskordin.core.data.IIdentified
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.event.IUserEvent
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.IMember
import org.tesserakt.diskordin.core.entity.IRole
import org.tesserakt.diskordin.core.entity.IUser

interface IGuildEvent : IEvent {
    val guild: IIdentified<IGuild>

    interface Eager : IGuildEvent {
        override val guild: EagerIdentified<IGuild>
    }

    interface Deferred : IGuildEvent {
        override val guild: DeferredIdentified<IGuild>
    }
}

interface IMemberEvent : IGuildEvent, IUserEvent {
    val member: IIdentified<IMember>
    override val user: IIdentified<IUser> get() = member

    interface Eager : IMemberEvent, IGuildEvent.Eager {
        override val member: EagerIdentified<IMember>
    }

    interface Deferred : IMemberEvent, IGuildEvent.Deferred {
        override val member: DeferredIdentified<IMember>
    }
}

interface IRoleEvent : IGuildEvent {
    val role: IIdentified<IRole>

    interface Eager : IRoleEvent, IGuildEvent.Eager {
        override val role: EagerIdentified<IRole>
    }

    interface Deferred : IRoleEvent, IGuildEvent.Deferred {
        override val role: DeferredIdentified<IRole>
    }
}
