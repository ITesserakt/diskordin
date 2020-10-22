package org.tesserakt.diskordin.core.data.event.guild

import arrow.core.ForId
import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.event.IUserEvent
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.identifyId
import org.tesserakt.diskordin.core.data.invoke
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.cache
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.Ban

class BanEvent(raw: Ban) : IGuildEvent<ForIO>, IUserEvent<ForId> {
    override val guild = raw.guildId.identify<IGuild> { client.getGuild(it) }
    override val user = raw.user.id identifyId { raw.user.unwrap() }

    init {
        cache[user.id] = user()
    }
}
