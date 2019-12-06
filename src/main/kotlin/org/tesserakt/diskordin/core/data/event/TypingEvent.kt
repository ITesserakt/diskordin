package org.tesserakt.diskordin.core.data.event

import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.IMessageChannel
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.Typing
import java.time.Instant

class TypingEvent(raw: Typing) : IEvent {
    val channel = raw.channelId identify { client.getChannel(it).map { it as IMessageChannel } }
    val guild = raw.guildId?.identify { client.getGuild(it) }
    val user = raw.userId identify { client.getUser(it) }
    val timestamp: Instant = Instant.ofEpochSecond(raw.timestamp)
}
