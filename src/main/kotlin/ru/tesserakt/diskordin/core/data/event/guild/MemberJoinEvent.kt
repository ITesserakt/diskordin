package ru.tesserakt.diskordin.core.data.event.guild

import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.gateway.json.events.MemberJoin

class MemberJoinEvent(raw: MemberJoin) : IEvent {
    //val member = raw.member.user.id combine { raw.member.unwrap(raw.guildId) } //FIXME unexpected NPE
}