package org.tesserakt.diskordin.core.data.event.guild

import arrow.core.extensions.id.applicative.just
import arrow.core.extensions.id.comonad.extract
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.Ban
import org.tesserakt.diskordin.rest.storage.GlobalEntityCache

class UnbanEvent(raw: Ban) : IEvent {
    val guild = raw.guildId identify { client.getGuild(it) }
    val user = raw.user.id identify { raw.user.unwrap().just() }

    init {
        GlobalEntityCache[user.id] = user().extract()
    }
}
