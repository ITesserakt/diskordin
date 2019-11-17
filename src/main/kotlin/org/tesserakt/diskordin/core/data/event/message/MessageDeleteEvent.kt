package org.tesserakt.diskordin.core.data.event.message

import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.ITextChannel
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.MessageDelete

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
