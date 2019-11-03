package ru.tesserakt.diskordin.core.data.event

import ru.tesserakt.diskordin.core.data.identify
import ru.tesserakt.diskordin.core.entity.IMessageChannel
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.Typing
import java.time.Instant

class TypingEvent(raw: Typing) : IEvent {
    val channel = raw.channelId identify { client.getChannel(it).bind() as IMessageChannel }
    val guild = raw.guildId?.identify { client.getGuild(it).bind() }
    val user = raw.userId identify { client.getUser(it).bind() }
    val timestamp: Instant = Instant.ofEpochSecond(raw.timestamp)
}
