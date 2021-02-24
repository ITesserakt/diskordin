package org.tesserakt.diskordin.core.data.event.channel

import org.tesserakt.diskordin.core.data.DeferredIdentified
import org.tesserakt.diskordin.core.data.EagerIdentified
import org.tesserakt.diskordin.core.data.IIdentified
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.entity.IChannel

interface IChannelEvent : IEvent {
    val channel: IIdentified<IChannel>

    interface Eager : IChannelEvent {
        override val channel: EagerIdentified<IChannel>
    }

    interface Deferred : IChannelEvent {
        override val channel: DeferredIdentified<IChannel>
    }
}