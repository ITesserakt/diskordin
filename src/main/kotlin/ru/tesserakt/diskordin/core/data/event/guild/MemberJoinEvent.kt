package ru.tesserakt.diskordin.core.data.event.guild

import ru.tesserakt.diskordin.core.data.combine
import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.data.json.response.JoinMemberResponse
import ru.tesserakt.diskordin.core.data.json.response.unwrap

class MemberJoinEvent(raw: JoinMemberResponse) : IEvent {
    val member = raw.user.id.combine { raw.unwrap() } //FIXME unexpected NPE
}