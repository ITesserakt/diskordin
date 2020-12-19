package org.tesserakt.diskordin.core.data.event

import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.event.channel.IChannelEvent
import org.tesserakt.diskordin.core.data.event.guild.IGuildEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.IChannel
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.WebhooksUpdate

class WebhooksUpdateEvent(raw: WebhooksUpdate) : IGuildEvent<ForIO>, IChannelEvent<ForIO> {
    override val guild = raw.guildId.identify<IGuild> { client.getGuild(it) }
    override val channel = raw.channelId.identify<IChannel> { client.getChannel(it) }
}
