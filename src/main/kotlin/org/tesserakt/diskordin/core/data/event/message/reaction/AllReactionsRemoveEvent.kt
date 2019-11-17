package org.tesserakt.diskordin.core.data.event.message.reaction

import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.IMessageChannel
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.AllReactionsRemove

class AllReactionsRemoveEvent(raw: AllReactionsRemove) : IEvent {
    val guild = raw.guildId?.identify { client.getGuild(it).bind() }
    val channel = raw.channelId identify { client.getChannel(it).bind() as IMessageChannel }
    val message = raw.messageId identify { channel().bind().messages.bind().first { message -> message.id == it } }
}
