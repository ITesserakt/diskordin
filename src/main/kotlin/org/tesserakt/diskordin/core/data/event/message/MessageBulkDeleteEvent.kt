package org.tesserakt.diskordin.core.data.event.message

import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.MessageBulkDelete

class MessageBulkDeleteEvent(raw: MessageBulkDelete) : IEvent {
    val deletedMessages = raw.ids
    val channel = raw.channelId identify { client.getChannel(it).bind() }
    val guild = raw.guildId?.identify { client.getGuild(it).bind() }
}
