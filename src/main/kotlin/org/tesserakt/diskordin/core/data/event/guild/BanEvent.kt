package org.tesserakt.diskordin.core.data.event.guild

import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.Ban

class BanEvent(raw: Ban) : IEvent {
    val guild = raw.guildId identify { client.getGuild(it).bind() }
    val user = raw.user.id identify { raw.user.unwrap() }
}
