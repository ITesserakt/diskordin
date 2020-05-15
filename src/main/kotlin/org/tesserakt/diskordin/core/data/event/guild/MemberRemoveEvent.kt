package org.tesserakt.diskordin.core.data.event.guild

import arrow.core.ForId
import arrow.core.extensions.id.applicative.just
import arrow.core.extensions.id.comonad.extract
import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.event.IUserEvent
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.invoke
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.cache
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.MemberRemove

class MemberRemoveEvent(raw: MemberRemove) : IGuildEvent<ForIO>, IUserEvent<ForId> {
    override val guild = raw.guildId identify { client.getGuild(it) }
    override val user = raw.user.id identify { raw.user.unwrap().just() }

    init {
        cache[user.id] = user().extract()
    }
}
