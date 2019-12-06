package org.tesserakt.diskordin.core.data.event

import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.VoiceServerUpdate

class VoiceServerUpdateEvent(raw: VoiceServerUpdate) : IEvent {
    val token = raw.token
    val guild = raw.guildId identify { client.getGuild(it) }
    val endpoint = raw.endpoint
}
