package ru.tesserakt.diskordin.core.data.event

import ru.tesserakt.diskordin.core.data.identify
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.VoiceServerUpdate

class VoiceServerUpdateEvent(raw: VoiceServerUpdate) : IEvent {
    val token = raw.token
    val guild = raw.guildId identify { client.getGuild(it).bind() }
    val endpoint = raw.endpoint
}
