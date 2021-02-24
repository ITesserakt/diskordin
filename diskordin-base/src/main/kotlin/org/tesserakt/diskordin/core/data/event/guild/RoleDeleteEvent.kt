package org.tesserakt.diskordin.core.data.event.guild

import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.RoleDelete

class RoleDeleteEvent(raw: RoleDelete) : IGuildEvent.Deferred {
    override val guild = raw.guildId deferred { client.getGuild(it) }
    val roleId = raw.roleId
}
