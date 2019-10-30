package ru.tesserakt.diskordin.core.data.event.message

import ru.tesserakt.diskordin.core.data.combine
import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.MessageDelete

class MessageDeleteEvent(raw: MessageDelete) : IEvent {
    val messageId = raw.id
    val guild = raw.guildId?.combine { client.getGuild(it) }
    val channel = raw.channelId combine {
        @Suppress("ComplexRedundantLet")
        when (guild) {
            null -> client.getChannel(it)
            else -> guild.let { it1 -> it1() }.getChannel(it) //Bug in type checker
        }
    }
}
