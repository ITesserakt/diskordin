package org.tesserakt.diskordin.core.data.event

import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.data.event.guild.IGuildEvent
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.VoiceServerUpdate

class VoiceServerUpdateEvent(raw: VoiceServerUpdate) : IGuildEvent.Deferred {
    val token = raw.token
    override val guild = raw.guildId deferred { client.getGuild(it) }
    val endpoint = raw.endpoint
}
