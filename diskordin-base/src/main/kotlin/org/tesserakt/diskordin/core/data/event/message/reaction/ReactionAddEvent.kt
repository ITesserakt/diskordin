package org.tesserakt.diskordin.core.data.event.message.reaction

import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.data.event.IUserEvent
import org.tesserakt.diskordin.core.data.event.channel.IChannelEvent
import org.tesserakt.diskordin.core.data.event.message.IMessageEvent
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IMessageChannel
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.Reaction

class ReactionAddEvent(raw: Reaction) : IMessageEvent.Deferred, IUserEvent.Deferred, IChannelEvent.Deferred {
    override val user = raw.userId deferred (client::getUser)
    override val channel = raw.channelId deferred { client.getChannel(it) as IMessageChannel }
    val guild = raw.guildId?.deferred { client.getGuild(it) }
    override val message = raw.messageId deferred { id ->
        channel().cachedMessages.first { it.id == id }
    }
    val emoji = raw.emoji.unwrap(guild?.id)
}
