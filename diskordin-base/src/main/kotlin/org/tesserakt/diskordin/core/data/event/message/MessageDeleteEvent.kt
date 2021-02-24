package org.tesserakt.diskordin.core.data.event.message

import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.data.event.channel.IChannelEvent
import org.tesserakt.diskordin.core.entity.IMessageChannel
import org.tesserakt.diskordin.core.entity.ITextChannel
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.MessageDelete

class MessageDeleteEvent(raw: MessageDelete) : IChannelEvent.Deferred {
    val messageId = raw.id
    val guild = raw.guildId?.deferred { client.getGuild(it) }
    override val channel = raw.channelId deferred {
        when (guild) {
            null -> client.getChannel(it) as IMessageChannel
            else -> guild.invoke().getChannel<ITextChannel>(it)
        }
    }
}
