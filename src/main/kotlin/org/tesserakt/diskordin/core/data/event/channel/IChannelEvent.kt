package org.tesserakt.diskordin.core.data.event.channel

import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.entity.IChannel

interface IChannelEvent<F> : IEvent {
    val channel: IdentifiedF<F, out IChannel>
}