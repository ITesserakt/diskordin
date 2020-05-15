package org.tesserakt.diskordin.core.data.event.message.reaction

import arrow.core.toOption
import arrow.fx.ForIO
import arrow.fx.extensions.io.monad.map
import org.tesserakt.diskordin.core.data.event.IUserEvent
import org.tesserakt.diskordin.core.data.event.channel.IChannelEvent
import org.tesserakt.diskordin.core.data.event.message.IMessageEvent
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.invoke
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.ICustomEmoji
import org.tesserakt.diskordin.core.entity.IMessageChannel
import org.tesserakt.diskordin.core.entity.cache
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.Reaction

class ReactionAddEvent(raw: Reaction) : IMessageEvent<ForIO>, IUserEvent<ForIO>, IChannelEvent<ForIO> {
    override val user = raw.userId identify { client.getUser(it) }
    override val channel = raw.channelId identify { client.getChannel(it).map { it as IMessageChannel } }
    val guild = raw.guildId?.identify { client.getGuild(it) }
    override val message = raw.messageId identify { id ->
        channel().map { channel -> channel.cachedMessages.first { it.id == id } }
    }
    val emoji = raw.emoji.unwrap(guild?.id.toOption())

    init {
        if (emoji is ICustomEmoji)
            cache[emoji.id] = emoji
    }
}
