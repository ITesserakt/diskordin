package ru.tesserakt.diskordin.core.data.event.message.reaction

import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.data.identify
import ru.tesserakt.diskordin.core.data.json.response.unwrap
import ru.tesserakt.diskordin.core.entity.IMessageChannel
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.Reaction

class ReactionAddEvent(raw: Reaction) : IEvent {
    val user = raw.userId identify { client.getUser(it).bind() }
    val channel = raw.channelId identify { client.getChannel(it).bind() as IMessageChannel }
    val guild = raw.guildId?.identify() { client.getGuild(it).bind() }
    val message =
        raw.messageId identify { channel().bind().messages.bind().first { message -> message.id == it } }
    val emoji = raw.emoji.unwrap()
}
