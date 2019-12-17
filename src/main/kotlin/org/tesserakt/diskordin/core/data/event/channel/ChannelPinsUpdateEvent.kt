package org.tesserakt.diskordin.core.data.event.channel

import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.ChannelPinsUpdate

class ChannelPinsUpdateEvent(raw: ChannelPinsUpdate) : IChannelEvent<ForIO> {
    val guild = raw.guildId?.identify { client.getGuild(it) }
    override val channel = raw.channelId identify { client.getChannel(it) }
    val lastPinTimestamp = raw.lastPinTimestamp
}
