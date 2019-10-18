package ru.tesserakt.diskordin.core.data.event.guild

import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.Ban
import ru.tesserakt.diskordin.util.combine

class UnbanEvent(raw: Ban) : IEvent {
    val guild = raw.guildId combine { client.findGuild(it)!! }
    val user = raw.user.id.asSnowflake() combine { raw.user.unwrap() }
}
