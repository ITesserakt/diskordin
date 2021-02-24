package org.tesserakt.diskordin.core.data.event

import kotlinx.datetime.Instant
import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.data.event.channel.IChannelEvent
import org.tesserakt.diskordin.core.entity.IMessageChannel
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.Typing

class TypingEvent(raw: Typing) : IUserEvent.Deferred, IChannelEvent.Deferred {
    override val channel = raw.channelId deferred { client.getChannel(it) as IMessageChannel }
    val guild = raw.guildId?.deferred { client.getGuild(it) }
    override val user = raw.userId deferred { client.getUser(it) }
    val timestamp: Instant = Instant.fromEpochSeconds(raw.timestamp)
}
