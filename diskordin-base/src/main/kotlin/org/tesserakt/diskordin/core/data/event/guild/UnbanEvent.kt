package org.tesserakt.diskordin.core.data.event.guild

import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.data.eager
import org.tesserakt.diskordin.core.data.event.IUserEvent
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.Ban

class UnbanEvent(raw: Ban) : IGuildEvent.Deferred, IUserEvent.Eager {
    override val guild = raw.guildId deferred { client.getGuild(it) }
    override val user = raw.user.id eager { raw.user.unwrap() }
}
