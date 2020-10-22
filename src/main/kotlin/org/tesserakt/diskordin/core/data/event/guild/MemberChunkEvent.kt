package org.tesserakt.diskordin.core.data.event.guild

import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.cache
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.MemberChunk

class MemberChunkEvent(raw: MemberChunk) : IGuildEvent<ForIO> {
    override val guild = raw.guildId.identify<IGuild> { client.getGuild(it) }
    val members = raw.members.map { it.unwrap(guild.id) }
    val notFoundMembers = raw.notFound ?: emptyArray()

    init {
        cache += members.map { it.id to it }
    }
}
