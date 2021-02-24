package org.tesserakt.diskordin.core.data.event.message.reaction

import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.data.event.channel.IChannelEvent
import org.tesserakt.diskordin.core.data.event.message.IMessageEvent
import org.tesserakt.diskordin.core.entity.IMessageChannel
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.AllReactionsRemove

class AllReactionsRemoveEvent(raw: AllReactionsRemove) : IMessageEvent.Deferred, IChannelEvent.Deferred {
    val guild = raw.guildId?.deferred { client.getGuild(it) }
    override val channel = raw.channelId deferred { client.getChannel(it) as IMessageChannel }
    override val message = raw.messageId deferred { id ->
        client.getMessage(channel.id, id)
    }
}
