package org.tesserakt.diskordin.core.data.event.lifecycle

import org.tesserakt.diskordin.core.data.event.IEvent

class InvalidSessionEvent(raw: Boolean) : IEvent {
    val canResume = raw
}
