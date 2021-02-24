package org.tesserakt.diskordin.core.data.event.guild

import org.tesserakt.diskordin.core.data.EagerIdentified
import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.data.eager
import org.tesserakt.diskordin.core.data.json.response.JoinMemberResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.client

class MemberJoinEvent(raw: JoinMemberResponse) : IMemberEvent {
    override val member = raw.user.id.eager { raw.unwrap() }
    override val user: EagerIdentified<IUser> = member
    override val guild = raw.guildId deferred { client.getGuild(it) }
}