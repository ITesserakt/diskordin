package org.tesserakt.diskordin.core.data.event.guild

import arrow.core.extensions.id.applicative.just
import arrow.core.extensions.id.comonad.extract
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.GuildResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.rest.storage.GlobalEntityCache

class GuildUpdateEvent(raw: GuildResponse) : IEvent {
    val guild = raw.id identify { raw.unwrap().just() }

    init {
        GlobalEntityCache[guild.id] = guild().extract()
    }
}
