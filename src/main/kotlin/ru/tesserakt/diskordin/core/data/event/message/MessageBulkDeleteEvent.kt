package ru.tesserakt.diskordin.core.data.event.message

import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.MessageBulkDelete
import ru.tesserakt.diskordin.util.combine

class MessageBulkDeleteEvent(raw: MessageBulkDelete) : IEvent {
    val deletedMessages = raw.ids
    val channel = raw.channelId combine { client.findChannel(it)!! }
    val guild = raw.guildId?.combine { client.findGuild(it)!! }
}
