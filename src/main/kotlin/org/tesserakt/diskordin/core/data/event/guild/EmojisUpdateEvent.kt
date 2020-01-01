package org.tesserakt.diskordin.core.data.event.guild

import arrow.core.some
import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.cache
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.Emojis

class EmojisUpdateEvent(raw: Emojis) : IEvent, IGuildEvent<ForIO> {
    override val guild = raw.guildId identify { client.getGuild(it) }
    val emojis = raw.emojis.map { it.unwrap(guild.id.some()) }

    init {
        cache += emojis.map { it.id to it }
    }
}
