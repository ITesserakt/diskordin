package org.tesserakt.diskordin.core.data.event.guild

import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.MemberChunk
import org.tesserakt.diskordin.rest.storage.GlobalMemberCache

class MemberChunkEvent(raw: MemberChunk) : IGuildEvent<ForIO> {
    override val guild = raw.guildId identify { client.getGuild(it) }
    val members = raw.members.map { it.unwrap(guild.id) }
    val notFoundMembers = raw.notFound ?: emptyArray()

    init {
        GlobalMemberCache += members.map { guild.id to it.id to it }
    }
}
