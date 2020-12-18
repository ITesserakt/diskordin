package org.tesserakt.diskordin.core.data.event.guild

import arrow.core.ForId
import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.identifyId
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.RoleUpdate

class RoleUpdateEvent(raw: RoleUpdate) : IRoleEvent<ForId, ForIO> {
    override val guild = raw.guildId.identify<IGuild> { client.getGuild(it) }
    override val role = raw.role.id identifyId { raw.role.unwrap(guild.id) }
}
