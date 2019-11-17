package org.tesserakt.diskordin.core.data.event.guild

import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.json.response.GuildResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap

class GuildUpdateEvent(raw: GuildResponse) : IEvent {
    val guild = raw.unwrap()
}
