package org.tesserakt.diskordin.core.data.event.message

import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.data.event.channel.IChannelEvent
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.MessageBulkDelete

class MessageBulkDeleteEvent(raw: MessageBulkDelete) : IChannelEvent.Deferred {
    val deletedMessages = raw.ids
    override val channel = raw.channelId.deferred { client.getChannel(it) }
    val guild = raw.guildId?.deferred { client.getGuild(it) }
}
