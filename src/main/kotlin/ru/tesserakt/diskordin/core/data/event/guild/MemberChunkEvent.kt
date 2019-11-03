package ru.tesserakt.diskordin.core.data.event.guild

import kotlinx.coroutines.flow.asFlow
import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.data.identify
import ru.tesserakt.diskordin.core.data.json.response.unwrap
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.MemberChunk

class MemberChunkEvent(raw: MemberChunk) : IEvent {
    val guild = raw.guildId identify { client.getGuild(it).bind() }
    val members = raw.members.map { it.unwrap(guild.id) }.asFlow()
    val notFoundMembers = raw.notFound ?: emptyArray()
}
