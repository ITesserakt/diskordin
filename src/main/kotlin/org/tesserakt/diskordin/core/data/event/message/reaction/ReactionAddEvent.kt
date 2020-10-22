package org.tesserakt.diskordin.core.data.event.message.reaction

import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.event.IUserEvent
import org.tesserakt.diskordin.core.data.event.channel.IChannelEvent
import org.tesserakt.diskordin.core.data.event.message.IMessageEvent
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.invoke
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.gateway.json.events.Reaction

class ReactionAddEvent(raw: Reaction) : IMessageEvent<ForIO>, IUserEvent<ForIO>, IChannelEvent<ForIO> {
    override val user = raw.userId.identify<IUser> { client.getUser(it) }
    override val channel = raw.channelId.identify<IMessageChannel> { client.getChannel(it) as IMessageChannel }
    val guild = raw.guildId?.identify<IGuild> { client.getGuild(it) }
    override val message = raw.messageId.identify<IMessage> { id ->
        channel().cachedMessages.first { it.id == id }
    }
    val emoji = raw.emoji.unwrap(guild?.id)

    init {
        if (emoji is ICustomEmoji)
            cache[emoji.id] = emoji
    }
}
