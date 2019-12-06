package org.tesserakt.diskordin.core.data.event.guild

import kotlinx.coroutines.flow.asFlow
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.MemberChunk

class MemberChunkEvent(raw: MemberChunk) : IEvent {
    val guild = raw.guildId identify { client.getGuild(it) }
    val members = raw.members.map { it.unwrap(guild.id) }.asFlow()
    val notFoundMembers = raw.notFound ?: emptyArray()
}
