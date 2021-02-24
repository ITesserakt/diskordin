package org.tesserakt.diskordin.core.data.event.channel

import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.ChannelPinsUpdate

class ChannelPinsUpdateEvent(raw: ChannelPinsUpdate) : IChannelEvent.Deferred {
    val guild = raw.guildId?.deferred { client.getGuild(it) }
    override val channel = raw.channelId deferred { client.getChannel(it) }
    val lastPinTimestamp = raw.lastPinTimestamp
}
