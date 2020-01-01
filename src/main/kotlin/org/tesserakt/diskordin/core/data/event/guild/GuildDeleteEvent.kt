package org.tesserakt.diskordin.core.data.event.guild

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.entity.cache

class GuildDeleteEvent(raw: Pair<Snowflake, Boolean>) : IEvent {
    val guildId = raw.first
    val isRemovedOrLeave = !raw.second
    val isUnavailable = raw.second

    init {
        cache -= guildId
    }
}
