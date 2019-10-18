package ru.tesserakt.diskordin.core.data.event.lifecycle

import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.gateway.json.events.Ready

class ReadyEvent(raw: Ready) : IEvent {
    val gatewayProtocolVersion = raw.gatewayProtocolVersion
    val self = raw.user.unwrap("Self")
    val guilds = raw.guilds
    val sessionId = raw.sessionId
}