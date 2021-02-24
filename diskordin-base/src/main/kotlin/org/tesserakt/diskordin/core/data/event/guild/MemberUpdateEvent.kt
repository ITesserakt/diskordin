package org.tesserakt.diskordin.core.data.event.guild

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapNotNull
import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.data.eager
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.MemberUpdate

class MemberUpdateEvent(raw: MemberUpdate) : IGuildEvent.Deferred {
    override val guild = raw.guildId deferred { client.getGuild(it) }

    val roles = flowOf(*raw.roles)
        .mapNotNull { guild().getRole(it) }

    val user = raw.user.id eager { raw.user.unwrap() }

    val nick = raw.nick
}
