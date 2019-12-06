package org.tesserakt.diskordin.core.data.event.message.reaction

import arrow.core.toOption
import arrow.fx.extensions.io.monad.flatMap
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IMessageChannel
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.Reaction

class ReactionRemoveEvent(raw: Reaction) : IEvent {
    val user = raw.userId identify { client.getUser(it) }
    val channel = raw.channelId identify { client.getChannel(it).map { it as IMessageChannel } }
    val guild = raw.guildId?.identify { client.getGuild(it) }
    val message = raw.messageId identify { id ->
        channel()
            .flatMap { it.messages }
            .map {
                it.first { message -> message.id == id }
            }
    }
    val emoji = raw.emoji.unwrap(guild?.id.toOption())
}
