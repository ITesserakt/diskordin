package ru.tesserakt.diskordin.core.data.event

import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.WebhooksUpdate
import ru.tesserakt.diskordin.util.combine

class WebhooksUpdateEvent(raw: WebhooksUpdate) : IEvent {
    val guild = raw.guildId combine { client.findGuild(it)!! }
    val channel = raw.channelId combine { client.findChannel(it)!! }
}
