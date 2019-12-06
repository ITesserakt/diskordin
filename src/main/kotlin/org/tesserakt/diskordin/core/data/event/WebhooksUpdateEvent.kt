package org.tesserakt.diskordin.core.data.event

import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.WebhooksUpdate

class WebhooksUpdateEvent(raw: WebhooksUpdate) : IEvent {
    val guild = raw.guildId identify { client.getGuild(it) }
    val channel = raw.channelId identify { client.getChannel(it) }
}
