package ru.tesserakt.diskordin.core.data.event.guild

import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.Emojis
import ru.tesserakt.diskordin.util.combine

class EmojisUpdateEvent(raw: Emojis) : IEvent {
    val guild = raw.guildId combine { client.findGuild(it)!! }
    val emojis = raw.emojis.map { it.unwrap(guild.id) }
}