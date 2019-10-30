package ru.tesserakt.diskordin.core.data.event

import ru.tesserakt.diskordin.core.data.combine
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.WebhooksUpdate

class WebhooksUpdateEvent(raw: WebhooksUpdate) : IEvent {
    val guild = raw.guildId combine { client.getGuild(it) }
    val channel = raw.channelId combine { client.getChannel(it) }
}
