package ru.tesserakt.diskordin.core.data.event.message

import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.data.identify
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.MessageBulkDelete

class MessageBulkDeleteEvent(raw: MessageBulkDelete) : IEvent {
    val deletedMessages = raw.ids
    val channel = raw.channelId identify { client.getChannel(it).bind() }
    val guild = raw.guildId?.identify { client.getGuild(it).bind() }
}
