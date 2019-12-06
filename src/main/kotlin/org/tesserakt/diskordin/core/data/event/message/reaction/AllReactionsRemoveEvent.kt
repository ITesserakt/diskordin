package org.tesserakt.diskordin.core.data.event.message.reaction

import arrow.fx.extensions.io.monad.flatMap
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.IMessageChannel
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.AllReactionsRemove

class AllReactionsRemoveEvent(raw: AllReactionsRemove) : IEvent {
    val guild = raw.guildId?.identify { client.getGuild(it) }
    val channel = raw.channelId identify { client.getChannel(it).map { it as IMessageChannel } }
    val message = raw.messageId identify { id ->
        channel()
            .flatMap { it.messages }
            .map {
                it.first { message -> message.id == id }
            }
    }
}
