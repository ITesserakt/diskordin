package org.tesserakt.diskordin.core.data.event.guild

import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.Integrations

class IntegrationsUpdateEvent(raw: Integrations) : IGuildEvent.Deferred {
    override val guild = raw.guildId deferred { client.getGuild(it) }
}
