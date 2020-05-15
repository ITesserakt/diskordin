package org.tesserakt.diskordin.core.data.event.guild

import arrow.core.ForId
import arrow.core.extensions.id.applicative.just
import arrow.core.extensions.id.comonad.extract
import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.invoke
import org.tesserakt.diskordin.core.data.json.response.JoinMemberResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.cache
import org.tesserakt.diskordin.core.entity.client

class MemberJoinEvent(raw: JoinMemberResponse) : IMemberEvent<ForId, ForIO> {
    override val member = raw.user.id.identify { raw.unwrap().just() }
    override val guild = raw.guildId identify { client.getGuild(it) }

    init {
        cache[member.id] = member().extract()
    }
}