package org.tesserakt.diskordin.core.data.event.message

import arrow.fx.ForIO
import arrow.fx.extensions.io.functor.map
import org.tesserakt.diskordin.core.data.event.channel.IChannelEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.IMessageChannel
import org.tesserakt.diskordin.core.entity.ITextChannel
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.MessageDelete
import org.tesserakt.diskordin.rest.storage.GlobalEntityCache

class MessageDeleteEvent(raw: MessageDelete) : IChannelEvent<ForIO> {
    val messageId = raw.id
    val guild = raw.guildId?.identify { client.getGuild(it) }
    override val channel = raw.channelId identify {
        when (guild) {
            null -> client.getChannel(it).map { channel -> channel as IMessageChannel }
            else -> guild.invoke().map { guild -> guild.getChannel<ITextChannel>(it) }
        }
    }

    init {
        GlobalEntityCache -= messageId
    }
}
