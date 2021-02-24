package org.tesserakt.diskordin.core.data.event.guild

import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.gateway.json.events.UnavailableGuild

class GuildDeleteEvent(raw: UnavailableGuild?) : IEvent {
    val guildId = raw?.id
    val isRemovedOrLeave = raw == null
    val isUnavailable = raw?.unavailable == true
}
