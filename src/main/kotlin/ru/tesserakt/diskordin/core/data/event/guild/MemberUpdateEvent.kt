package ru.tesserakt.diskordin.core.data.event.guild

import kotlinx.coroutines.flow.flow
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.MemberUpdate
import ru.tesserakt.diskordin.util.combine

class MemberUpdateEvent(raw: MemberUpdate) : IEvent {
    val guild = raw.guildId combine { client.findGuild(it)!! }
    val roles = flow {
        raw.roles.map { guild().getRole(it) }.forEach { emit(it) }
    }
    val user = raw.user.id.asSnowflake() combine { raw.user.unwrap().asMember(guild.id) }
    val nick = raw.nick
}
