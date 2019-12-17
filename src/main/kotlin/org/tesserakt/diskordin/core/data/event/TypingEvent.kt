package org.tesserakt.diskordin.core.data.event

import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.event.channel.IChannelEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.IMessageChannel
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.Typing
import java.time.Instant

class TypingEvent(raw: Typing) : IUserEvent<ForIO>, IChannelEvent<ForIO> {
    override val channel = raw.channelId identify { client.getChannel(it).map { it as IMessageChannel } }
    val guild = raw.guildId?.identify { client.getGuild(it) }
    override val user = raw.userId identify { client.getUser(it) }
    val timestamp: Instant = Instant.ofEpochSecond(raw.timestamp)
}
