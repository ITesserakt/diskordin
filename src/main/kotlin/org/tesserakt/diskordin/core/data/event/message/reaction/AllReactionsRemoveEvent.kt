package org.tesserakt.diskordin.core.data.event.message.reaction

import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.event.channel.IChannelEvent
import org.tesserakt.diskordin.core.data.event.message.IMessageEvent
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.IMessageChannel
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.AllReactionsRemove

class AllReactionsRemoveEvent(raw: AllReactionsRemove) : IMessageEvent<ForIO>, IChannelEvent<ForIO> {
    val guild = raw.guildId?.identify { client.getGuild(it) }
    override val channel = raw.channelId identify { client.getChannel(it).map { it as IMessageChannel } }
    override val message = raw.messageId identify { id ->
        client.getMessage(channel.id, id)
    }
}
