package org.tesserakt.diskordin.core.data.event.guild

import arrow.core.ForId
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.identifyId
import org.tesserakt.diskordin.core.data.json.response.GuildResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap

class GuildCreateEvent(raw: GuildResponse) : IEvent, IGuildEvent<ForId> {
    override val guild = raw.id identifyId { raw.unwrap() }
}
