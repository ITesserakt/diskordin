package ru.tesserakt.diskordin.core.data.event

import ru.tesserakt.diskordin.core.data.identify
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.WebhooksUpdate

class WebhooksUpdateEvent(raw: WebhooksUpdate) : IEvent {
    val guild = raw.guildId identify { client.getGuild(it).bind() }
    val channel = raw.channelId identify { client.getChannel(it).bind() }
}
