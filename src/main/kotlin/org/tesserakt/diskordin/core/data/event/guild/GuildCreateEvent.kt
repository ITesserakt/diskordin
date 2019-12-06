package org.tesserakt.diskordin.core.data.event.guild

import arrow.core.extensions.id.applicative.just
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.GuildResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap

class GuildCreateEvent(raw: GuildResponse) : IEvent {
    val guild = raw.id identify { raw.unwrap().just() }
}
