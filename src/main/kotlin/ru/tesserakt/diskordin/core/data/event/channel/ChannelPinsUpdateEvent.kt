package ru.tesserakt.diskordin.core.data.event.channel

import ru.tesserakt.diskordin.core.data.combine
import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.ChannelPinsUpdate

class ChannelPinsUpdateEvent(raw: ChannelPinsUpdate) : IEvent {
    val guild = raw.guildId?.run { this combine { client.getGuild(it) } }
    val channel = raw.channelId combine { client.getChannel(it) }
    val lastPinTimestamp = raw.lastPinTimestamp
}
