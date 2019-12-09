package org.tesserakt.diskordin.core.data.event.message

import arrow.fx.extensions.io.monad.flatMap
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.IMessageChannel
import org.tesserakt.diskordin.core.entity.ITextChannel
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.MessageDelete
import org.tesserakt.diskordin.rest.storage.GlobalEntityCache

class MessageDeleteEvent(raw: MessageDelete) : IEvent {
    val messageId = raw.id
    val guild = raw.guildId?.identify { client.getGuild(it) }
    val channel = raw.channelId identify {
        when (guild) {
            null -> client.getChannel(it).map { it as IMessageChannel }
            else -> guild.invoke().flatMap { guild -> guild.getChannel<ITextChannel>(it) }
        }
    }

    init {
        GlobalEntityCache -= messageId
    }
}
