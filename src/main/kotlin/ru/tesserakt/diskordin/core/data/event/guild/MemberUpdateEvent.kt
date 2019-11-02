package ru.tesserakt.diskordin.core.data.event.guild

import arrow.fx.IO
import arrow.fx.extensions.fx
import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.data.identify
import ru.tesserakt.diskordin.core.data.json.response.unwrap
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.MemberUpdate

class MemberUpdateEvent(raw: MemberUpdate) : IEvent {
    val guild = raw.guildId identify { client.getGuild(it).bind() }
    val roles = IO.fx {
        raw.roles.map { guild().bind().getRole(it).bind() }
    }
    val user = raw.user.id identify { raw.user.unwrap().asMember(guild.id).bind() }
    val nick = raw.nick
}
