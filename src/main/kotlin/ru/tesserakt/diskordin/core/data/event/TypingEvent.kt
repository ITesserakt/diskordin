package ru.tesserakt.diskordin.core.data.event

import ru.tesserakt.diskordin.core.data.combine
import ru.tesserakt.diskordin.core.entity.IMessageChannel
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.Typing
import java.time.Instant

class TypingEvent(raw: Typing) : IEvent {
    val channel = raw.channelId combine { client.getChannel(it) as IMessageChannel }
    val guild = raw.guildId?.combine { client.getGuild(it) }
    val user = raw.userId combine { client.getUser(it) }
    val timestamp: Instant = Instant.ofEpochSecond(raw.timestamp)
}
