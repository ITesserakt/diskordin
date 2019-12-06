package org.tesserakt.diskordin.core.data.event.guild

import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.Integrations

class IntegrationsUpdateEvent(raw: Integrations) : IEvent {
    val guild = raw.guildId identify { client.getGuild(it) }
}
