package ru.tesserakt.diskordin.core.data.event.guild

import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.data.identify
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.RoleDelete

class RoleDeleteEvent(raw: RoleDelete) : IEvent {
    val guild = raw.guildId identify { client.getGuild(it).bind() }
    val roleId = raw.roleId
}
