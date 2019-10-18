package ru.tesserakt.diskordin.core.data.event.message

import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.MessageDelete
import ru.tesserakt.diskordin.util.combine

class MessageDeleteEvent(raw: MessageDelete) : IEvent {
    val messageId = raw.id
    val guild = raw.guildId?.combine { client.findGuild(it)!! }
    val channel = raw.channelId combine {
        @Suppress("ComplexRedundantLet")
        when (guild) {
            null -> client.findChannel(it)!!
            else -> guild.let { it1 -> it1() }.getChannel(it) //Bug in type checker
        }
    }
}
