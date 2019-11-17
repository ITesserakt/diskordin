package org.tesserakt.diskordin.core.data.event.lifecycle

import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.gateway.json.events.Hello

class HelloEvent(raw: Hello) : IEvent {
    val heartbeatInterval = raw.heartbeatInterval
}