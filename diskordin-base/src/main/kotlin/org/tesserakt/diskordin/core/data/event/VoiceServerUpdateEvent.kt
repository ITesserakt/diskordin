package org.tesserakt.diskordin.core.data.event

import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.event.guild.IGuildEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.VoiceServerUpdate

class VoiceServerUpdateEvent(raw: VoiceServerUpdate) : IGuildEvent<ForIO> {
    val token = raw.token
    override val guild = raw.guildId.identify<IGuild> { client.getGuild(it) }
    val endpoint = raw.endpoint
}
