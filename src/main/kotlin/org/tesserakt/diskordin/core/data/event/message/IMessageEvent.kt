package org.tesserakt.diskordin.core.data.event.message

import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.entity.IMessage

interface IMessageEvent<F> : IEvent {
    val message: IdentifiedF<F, IMessage>
}