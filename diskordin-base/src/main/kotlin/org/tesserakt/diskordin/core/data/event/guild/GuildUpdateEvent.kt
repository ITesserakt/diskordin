package org.tesserakt.diskordin.core.data.event.guild

import org.tesserakt.diskordin.core.data.identified
import org.tesserakt.diskordin.core.data.json.response.GuildResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap

class GuildUpdateEvent(raw: GuildResponse) : IGuildEvent.Eager {
    override val guild = raw.unwrap().identified()
}
