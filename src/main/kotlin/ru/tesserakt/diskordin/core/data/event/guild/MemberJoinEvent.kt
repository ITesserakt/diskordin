package ru.tesserakt.diskordin.core.data.event.guild

import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.gateway.json.events.MemberJoin
import ru.tesserakt.diskordin.util.combine

class MemberJoinEvent(raw: MemberJoin) : IEvent {
    val member = raw.member.user.id.asSnowflake() combine { raw.member.unwrap(raw.guildId) }
}