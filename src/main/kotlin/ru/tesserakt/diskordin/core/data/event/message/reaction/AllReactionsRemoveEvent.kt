package ru.tesserakt.diskordin.core.data.event.message.reaction

import kotlinx.coroutines.flow.first
import ru.tesserakt.diskordin.core.data.combine
import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.entity.IMessageChannel
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.AllReactionsRemove

class AllReactionsRemoveEvent(raw: AllReactionsRemove) : IEvent {
    val guild = raw.guildId?.combine { client.getGuild(it) }
    val channel = raw.channelId combine { client.getChannel(it) as IMessageChannel }
    val message = raw.messageId combine { channel().messages.first { message -> message.id == it } }
}
