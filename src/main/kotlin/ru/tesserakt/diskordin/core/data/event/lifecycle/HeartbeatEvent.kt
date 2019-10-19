package ru.tesserakt.diskordin.core.data.event.lifecycle

import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.gateway.json.Heartbeat

class HeartbeatEvent(raw: Heartbeat) : IEvent {
    val v = raw.value
}
