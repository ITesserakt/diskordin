package ru.tesserakt.diskordin.core.data.event.channel

import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.ChannelPinsUpdate
import ru.tesserakt.diskordin.util.combine

class ChannelPinsUpdateEvent(raw: ChannelPinsUpdate) : IEvent {
    val guild = raw.guildId?.run { this combine { client.findGuild(it)!! } }
    val channel = raw.channelId combine { client.findChannel(it)!! }
    val lastPinTimestamp = raw.lastPinTimestamp
}
