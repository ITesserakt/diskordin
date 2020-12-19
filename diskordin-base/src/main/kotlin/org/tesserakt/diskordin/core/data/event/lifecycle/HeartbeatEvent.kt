package org.tesserakt.diskordin.core.data.event.lifecycle

import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.gateway.json.commands.Heartbeat

class HeartbeatEvent(raw: Heartbeat) : IEvent {
    val v = raw.value
}
