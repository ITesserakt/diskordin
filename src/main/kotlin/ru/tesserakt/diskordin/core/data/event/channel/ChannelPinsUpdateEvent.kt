package ru.tesserakt.diskordin.core.data.event.channel

import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.data.identify
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.ChannelPinsUpdate

class ChannelPinsUpdateEvent(raw: ChannelPinsUpdate) : IEvent {
    val guild = raw.guildId?.identify { client.getGuild(it).bind() }
    val channel = raw.channelId identify { client.getChannel(it).bind() }
    val lastPinTimestamp = raw.lastPinTimestamp
}
