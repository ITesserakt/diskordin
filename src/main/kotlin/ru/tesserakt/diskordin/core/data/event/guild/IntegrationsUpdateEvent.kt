package ru.tesserakt.diskordin.core.data.event.guild

import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.Integrations
import ru.tesserakt.diskordin.util.combine

class IntegrationsUpdateEvent(raw: Integrations) : IEvent {
    val guild = raw.guildId combine { client.findGuild(it)!! }
}
