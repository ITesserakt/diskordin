package org.tesserakt.diskordin.core.data.event.guild

import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.MemberChunk

class MemberChunkEvent(raw: MemberChunk) : IGuildEvent.Deferred {
    override val guild = raw.guildId deferred { client.getGuild(it) }
    val members = raw.members.map { it.unwrap(guild.id) }
    val notFoundMembers = raw.notFound.orEmpty()
}
