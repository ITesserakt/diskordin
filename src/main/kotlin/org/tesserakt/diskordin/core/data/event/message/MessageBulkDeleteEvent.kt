package org.tesserakt.diskordin.core.data.event.message

import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.event.channel.IChannelEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.IChannel
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.MessageBulkDelete

class MessageBulkDeleteEvent(raw: MessageBulkDelete) : IChannelEvent<ForIO> {
    val deletedMessages = raw.ids
    override val channel = raw.channelId.identify<IChannel> { client.getChannel(it) }
    val guild = raw.guildId?.identify<IGuild> { client.getGuild(it) }
}
