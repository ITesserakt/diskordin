package ru.tesserakt.diskordin.core.data.event.guild

import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.core.data.identify
import ru.tesserakt.diskordin.core.data.json.response.JoinMemberResponse
import ru.tesserakt.diskordin.core.data.json.response.unwrap

class MemberJoinEvent(raw: JoinMemberResponse) : IEvent {
    val member = raw.user.id.identify { raw.unwrap() }
}