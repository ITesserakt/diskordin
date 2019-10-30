package ru.tesserakt.diskordin.core.data.event

import ru.tesserakt.diskordin.core.data.combine
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.VoiceServerUpdate

class VoiceServerUpdateEvent(raw: VoiceServerUpdate) : IEvent {
    val token = raw.token
    val guild = raw.guildId combine { client.getGuild(it) }
    val endpoint = raw.endpoint
}
