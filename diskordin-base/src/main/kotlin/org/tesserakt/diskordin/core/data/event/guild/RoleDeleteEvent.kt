package org.tesserakt.diskordin.core.data.event.guild

import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.RoleDelete

class RoleDeleteEvent(raw: RoleDelete) : IGuildEvent<ForIO> {
    override val guild = raw.guildId.identify<IGuild> { client.getGuild(it) }
    val roleId = raw.roleId
}