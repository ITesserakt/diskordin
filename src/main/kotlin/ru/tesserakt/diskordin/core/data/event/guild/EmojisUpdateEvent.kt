package ru.tesserakt.diskordin.core.data.event.guild

import arrow.core.some
import ru.tesserakt.diskordin.core.data.combine
import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.data.json.response.unwrap
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.Emojis

class EmojisUpdateEvent(raw: Emojis) : IEvent {
    val guild = raw.guildId combine { client.getGuild(it) }
    val emojis = raw.emojis.map { it.unwrap(guild.id.some()) }
}
