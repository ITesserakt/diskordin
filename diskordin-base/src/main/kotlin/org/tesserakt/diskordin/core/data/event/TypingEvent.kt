package org.tesserakt.diskordin.core.data.event

import arrow.fx.ForIO
import kotlinx.datetime.Instant
import org.tesserakt.diskordin.core.data.event.channel.IChannelEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.IMessageChannel
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.Typing

class TypingEvent(raw: Typing) : IUserEvent<ForIO>, IChannelEvent<ForIO> {
    override val channel = raw.channelId.identify<IMessageChannel> { client.getChannel(it) as IMessageChannel }
    val guild = raw.guildId?.identify<IGuild> { client.getGuild(it) }
    override val user = raw.userId.identify<IUser> { client.getUser(it) }
    val timestamp: Instant = Instant.fromEpochSeconds(raw.timestamp)
}
