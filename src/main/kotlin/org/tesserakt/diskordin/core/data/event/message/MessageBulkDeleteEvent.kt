package org.tesserakt.diskordin.core.data.event.message

import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.event.channel.IChannelEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.cache
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.MessageBulkDelete

class MessageBulkDeleteEvent(raw: MessageBulkDelete) : IChannelEvent<ForIO> {
    val deletedMessages = raw.ids
    override val channel = raw.channelId identify { client.getChannel(it) }
    val guild = raw.guildId?.identify { client.getGuild(it) }

    init {
        cache -= deletedMessages
    }
}
