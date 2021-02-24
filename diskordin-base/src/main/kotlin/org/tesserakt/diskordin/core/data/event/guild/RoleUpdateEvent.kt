package org.tesserakt.diskordin.core.data.event.guild

import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.data.eager
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.RoleUpdate

class RoleUpdateEvent(raw: RoleUpdate) : IRoleEvent {
    override val guild = raw.guildId deferred { client.getGuild(it) }
    override val role = raw.role.id eager { raw.role.unwrap(guild.id) }
}
