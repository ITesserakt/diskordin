package org.tesserakt.diskordin.core.data.event.guild

import arrow.core.extensions.id.applicative.just
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.JoinMemberResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap

class MemberJoinEvent(raw: JoinMemberResponse) : IEvent {
    val member = raw.user.id.identify { raw.unwrap().just() }
}