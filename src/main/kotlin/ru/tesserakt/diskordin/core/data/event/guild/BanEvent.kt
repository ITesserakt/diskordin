package ru.tesserakt.diskordin.core.data.event.guild

import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.data.identify
import ru.tesserakt.diskordin.core.data.json.response.unwrap
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.Ban

class BanEvent(raw: Ban) : IEvent {
    val guild = raw.guildId identify { client.getGuild(it).bind() }
    val user = raw.user.id identify { raw.user.unwrap() }
}
