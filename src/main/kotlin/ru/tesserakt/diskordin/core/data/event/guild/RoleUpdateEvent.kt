package ru.tesserakt.diskordin.core.data.event.guild

import ru.tesserakt.diskordin.core.data.combine
import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.data.json.response.unwrap
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.RoleUpdate

class RoleUpdateEvent(raw: RoleUpdate) : IEvent {
    val guild = raw.guildId combine { client.getGuild(it) }
    val role = raw.role.id combine { raw.role.unwrap(guild.id) }
}
