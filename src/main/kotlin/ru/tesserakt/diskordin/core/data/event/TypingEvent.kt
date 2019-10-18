package ru.tesserakt.diskordin.core.data.event

import ru.tesserakt.diskordin.core.entity.IMessageChannel
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.Typing
import ru.tesserakt.diskordin.util.combine
import java.time.Instant

class TypingEvent(raw: Typing) : IEvent {
    val channel = raw.channelId combine { client.findChannel(it)!! as IMessageChannel }
    val guild = raw.guildId?.combine { client.findGuild(it)!! }
    val user = raw.userId combine { client.findUser(it)!! }
    val timestamp: Instant = Instant.ofEpochSecond(raw.timestamp)
}
