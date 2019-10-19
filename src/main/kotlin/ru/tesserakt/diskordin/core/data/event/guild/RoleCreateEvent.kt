package ru.tesserakt.diskordin.core.data.event.guild

import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.RoleCreate
import ru.tesserakt.diskordin.util.combine

class RoleCreateEvent(raw: RoleCreate) : IEvent {
    val guild = raw.guildId combine { client.findGuild(it)!! }
    val role = raw.role.id combine { raw.role.unwrap(guild.id) }
}
