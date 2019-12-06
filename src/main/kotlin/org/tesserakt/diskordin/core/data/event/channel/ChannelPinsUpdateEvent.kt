package org.tesserakt.diskordin.core.data.event.channel

import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.ChannelPinsUpdate

class ChannelPinsUpdateEvent(raw: ChannelPinsUpdate) : IEvent {
    val guild = raw.guildId?.identify { client.getGuild(it) }
    val channel = raw.channelId identify { client.getChannel(it) }
    val lastPinTimestamp = raw.lastPinTimestamp
}
