package ru.tesserakt.diskordin.core.data.event.lifecycle

import ru.tesserakt.diskordin.core.data.event.IEvent

class InvalidSessionEvent(raw: Boolean) : IEvent {
    val canResume = raw
}
