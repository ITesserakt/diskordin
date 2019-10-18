package ru.tesserakt.diskordin.core.data.event.lifecycle

import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.gateway.json.events.Hello

class HelloEvent(raw: Hello) : IEvent {
    val heartbeatInterval = raw.heartbeatInterval
}