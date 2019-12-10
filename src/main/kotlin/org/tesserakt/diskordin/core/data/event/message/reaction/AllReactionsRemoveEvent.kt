package org.tesserakt.diskordin.core.data.event.message.reaction

import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.IMessageChannel
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.AllReactionsRemove

class AllReactionsRemoveEvent(raw: AllReactionsRemove) : IEvent {
    val guild = raw.guildId?.identify { client.getGuild(it) }
    val channel = raw.channelId identify { client.getChannel(it).map { it as IMessageChannel } }
    val message = raw.messageId identify { id ->
       client.getMessage(channel.id, id)
    }
}
