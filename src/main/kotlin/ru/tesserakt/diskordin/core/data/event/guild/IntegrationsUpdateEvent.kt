package ru.tesserakt.diskordin.core.data.event.guild

import ru.tesserakt.diskordin.core.data.combine
import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.Integrations

class IntegrationsUpdateEvent(raw: Integrations) : IEvent {
    val guild = raw.guildId combine { client.getGuild(it) }
}
