package ru.tesserakt.diskordin.core.data.event.guild

import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.MemberRemove
import ru.tesserakt.diskordin.util.combine

class MemberRemoveEvent(raw: MemberRemove) : IEvent {
    val guild = raw.guildId combine { client.findGuild(it)!! }
    val user = raw.user.unwrap()
}
