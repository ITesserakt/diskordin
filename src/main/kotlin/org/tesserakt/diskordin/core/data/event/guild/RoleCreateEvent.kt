package org.tesserakt.diskordin.core.data.event.guild

import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.RoleCreate

class RoleCreateEvent(raw: RoleCreate) : IEvent {
    val guild = raw.guildId identify { client.getGuild(it).bind() }
    val role = raw.role.id identify { raw.role.unwrap(guild.id) }
}
