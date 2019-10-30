package ru.tesserakt.diskordin.core.data.event.guild

import kotlinx.coroutines.flow.flow
import ru.tesserakt.diskordin.core.data.combine
import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.MemberUpdate

class MemberUpdateEvent(raw: MemberUpdate) : IEvent {
    val guild = raw.guildId combine { client.getGuild(it) }
    val roles = flow {
        raw.roles.map { guild().getRole(it) }.forEach { emit(it) }
    }
    val user = raw.user.id combine { raw.user.unwrap().asMember(guild.id) }
    val nick = raw.nick
}
