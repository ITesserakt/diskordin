package org.tesserakt.diskordin.core.data.event.message

import org.tesserakt.diskordin.core.data.DeferredIdentified
import org.tesserakt.diskordin.core.data.EagerIdentified
import org.tesserakt.diskordin.core.data.IIdentified
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.entity.IMessage

interface IMessageEvent : IEvent {
    val message: IIdentified<IMessage>

    interface Eager : IMessageEvent {
        override val message: EagerIdentified<IMessage>
    }

    interface Deferred : IMessageEvent {
        override val message: DeferredIdentified<IMessage>
    }
}