package ru.tesserakt.diskordin.core.data.event.guild

import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.data.identify
import ru.tesserakt.diskordin.core.data.json.response.unwrap
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.RoleCreate

class RoleCreateEvent(raw: RoleCreate) : IEvent {
    val guild = raw.guildId identify { client.getGuild(it).bind() }
    val role = raw.role.id identify { raw.role.unwrap(guild.id) }
}
