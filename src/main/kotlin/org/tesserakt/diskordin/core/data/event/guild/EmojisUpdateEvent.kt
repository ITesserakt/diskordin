package org.tesserakt.diskordin.core.data.event.guild

import arrow.core.some
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.Emojis

class EmojisUpdateEvent(raw: Emojis) : IEvent {
    val guild = raw.guildId identify { client.getGuild(it).bind() }
    val emojis = raw.emojis.map { it.unwrap(guild.id.some()) }
}
