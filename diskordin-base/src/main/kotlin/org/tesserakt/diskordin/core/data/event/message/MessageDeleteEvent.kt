package org.tesserakt.diskordin.core.data.event.message

import arrow.fx.ForIO
import arrow.fx.fix
import org.tesserakt.diskordin.core.data.event.channel.IChannelEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.IMessageChannel
import org.tesserakt.diskordin.core.entity.ITextChannel
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.MessageDelete

class MessageDeleteEvent(raw: MessageDelete) : IChannelEvent<ForIO> {
    val messageId = raw.id
    val guild = raw.guildId?.identify<IGuild> { client.getGuild(it) }
    override val channel = raw.channelId.identify<IMessageChannel> {
        when (guild) {
            null -> client.getChannel(it) as IMessageChannel
            else -> guild.extract().fix().suspended().getChannel<ITextChannel>(it)
        }
    }
}
