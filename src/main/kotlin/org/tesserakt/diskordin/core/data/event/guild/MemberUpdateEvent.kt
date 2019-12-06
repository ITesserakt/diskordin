package org.tesserakt.diskordin.core.data.event.guild

import arrow.fx.IO
import arrow.fx.extensions.fx
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.MemberUpdate

class MemberUpdateEvent(raw: MemberUpdate) : IEvent {
    val guild = raw.guildId identify { client.getGuild(it) }
    val roles = IO.fx {
        raw.roles.map { guild().bind().getRole(it).bind() }
    }
    val user = raw.user.id identify { raw.user.unwrap().asMember(guild.id) }
    val nick = raw.nick
}
