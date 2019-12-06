package org.tesserakt.diskordin.core.data.event.guild

import arrow.core.extensions.id.applicative.just
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.RoleUpdate

class RoleUpdateEvent(raw: RoleUpdate) : IEvent {
    val guild = raw.guildId identify { client.getGuild(it) }
    val role = raw.role.id identify { raw.role.unwrap(guild.id).just() }
}
