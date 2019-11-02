package ru.tesserakt.diskordin.core.data.event.message

import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.data.identify
import ru.tesserakt.diskordin.core.entity.ITextChannel
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.MessageDelete

class MessageDeleteEvent(raw: MessageDelete) : IEvent {
    val messageId = raw.id
    val guild = raw.guildId?.identify() { client.getGuild(it).bind() }
    val channel = raw.channelId identify {
        @Suppress("ComplexRedundantLet")
        when (guild) {
            null -> client.getChannel(it).bind() as ITextChannel
            else -> guild.let { it1 -> it1() }.bind().getChannel<ITextChannel>(it).bind() //Bug in type checker
        }
    }
}
