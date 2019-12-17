package org.tesserakt.diskordin.core.data.event.guild

import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.RoleDelete
import org.tesserakt.diskordin.rest.storage.GlobalEntityCache

class RoleDeleteEvent(raw: RoleDelete) : IGuildEvent<ForIO> {
    override val guild = raw.guildId identify { client.getGuild(it) }
    val roleId = raw.roleId

    init {
        GlobalEntityCache -= roleId
    }
}
