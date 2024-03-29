package org.tesserakt.diskordin.core.data.event.lifecycle

import org.tesserakt.diskordin.core.data.eager
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.gateway.json.events.Ready

private typealias Current = Int
private typealias Total = Int

class ReadyEvent(raw: Ready) : IEvent {
    val gatewayProtocolVersion = raw.gatewayProtocolVersion
    val self = raw.user.id eager { raw.user.unwrap() }
    val guilds = raw.guilds
    val sessionId = raw.sessionId
    val shardData: Pair<Current, Total> = raw.shard?.let { it[0] to it[1] } ?: (0 to 1)
}