package org.tesserakt.diskordin.core.data.event.guild

import arrow.core.extensions.id.applicative.just
import arrow.core.extensions.id.comonad.extract
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.RoleCreate
import org.tesserakt.diskordin.rest.storage.GlobalEntityCache

class RoleCreateEvent(raw: RoleCreate) : IEvent {
    val guild = raw.guildId identify { client.getGuild(it) }
    val role = raw.role.id identify { raw.role.unwrap(guild.id).just() }

    init {
        GlobalEntityCache[role.id] = role().extract()
    }
}
