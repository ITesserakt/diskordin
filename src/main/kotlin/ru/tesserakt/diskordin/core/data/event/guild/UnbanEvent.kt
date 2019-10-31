package ru.tesserakt.diskordin.core.data.event.guild

import ru.tesserakt.diskordin.core.data.combine
import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.data.json.response.unwrap
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.Ban

class UnbanEvent(raw: Ban) : IEvent {
    val guild = raw.guildId combine { client.getGuild(it) }
    val user = raw.user.id combine { raw.user.unwrap() }
}
