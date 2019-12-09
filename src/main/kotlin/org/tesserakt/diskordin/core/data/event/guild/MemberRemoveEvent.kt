package org.tesserakt.diskordin.core.data.event.guild

import arrow.core.extensions.id.applicative.just
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.MemberRemove
import org.tesserakt.diskordin.rest.storage.GlobalMemberCache

class MemberRemoveEvent(raw: MemberRemove) : IEvent {
    val guild = raw.guildId identify { client.getGuild(it) }
    val user = raw.user.id identify { raw.user.unwrap().just() }

    init {
        GlobalMemberCache -= guild.id to user.id
    }
}
