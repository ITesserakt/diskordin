package org.tesserakt.diskordin.core.data.event.guild

import arrow.core.ForId
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.data.identifyId
import org.tesserakt.diskordin.core.data.invoke
import org.tesserakt.diskordin.core.data.json.response.GuildResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.cache

class GuildUpdateEvent(raw: GuildResponse) : IGuildEvent<ForId> {
    override val guild = raw.id identifyId { raw.unwrap() }

    init {
        cache[guild.id] = guild()
    }
}
