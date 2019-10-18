package ru.tesserakt.diskordin.core.data.event.message.reaction

import kotlinx.coroutines.flow.first
import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.entity.IMessageChannel
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.Reaction
import ru.tesserakt.diskordin.util.combine

class ReactionRemoveEvent(raw: Reaction) : IEvent {
    val user = raw.userId combine { client.findUser(it)!! }
    val channel = raw.channelId combine { client.findChannel(it)!! as IMessageChannel }
    val guild = raw.guildId?.combine { client.findGuild(it)!! }
    val message = raw.messageId combine { channel().messages.first { message -> message.id == it } }
    val emoji = raw.emoji.unwrap()
}
