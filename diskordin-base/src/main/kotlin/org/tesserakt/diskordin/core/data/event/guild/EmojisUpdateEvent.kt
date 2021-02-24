package org.tesserakt.diskordin.core.data.event.guild

import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.Emojis

class EmojisUpdateEvent(raw: Emojis) : IEvent, IGuildEvent.Deferred {
    override val guild = raw.guildId.deferred { client.getGuild(it) }
    val emojis = raw.emojis.map { it.unwrap(guild.id) }
}
