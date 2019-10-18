package ru.tesserakt.diskordin.core.data.event.guild

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.event.IEvent

class GuildDeleteEvent(raw: Pair<Snowflake, Boolean>) : IEvent {
    val guildId = raw.first
    val isRemovedOrLeave = !raw.second
    val isUnavailable = raw.second
}
