package org.tesserakt.diskordin.core.data.event.channel

import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.IChannel
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.ChannelPinsUpdate

class ChannelPinsUpdateEvent(raw: ChannelPinsUpdate) : IChannelEvent<ForIO> {
    val guild = raw.guildId?.identify<IGuild> { client.getGuild(it) }
    override val channel = raw.channelId.identify<IChannel> { client.getChannel(it) }
    val lastPinTimestamp = raw.lastPinTimestamp
}
