package ru.tesserakt.diskordin.core.data.event.guild

import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.RoleUpdate
import ru.tesserakt.diskordin.util.combine

class RoleUpdateEvent(raw: RoleUpdate) : IEvent {
    val guild = raw.guildId combine { client.findGuild(it)!! }
    val role = raw.role.id.asSnowflake() combine { raw.role.unwrap(guild.id) }
}
