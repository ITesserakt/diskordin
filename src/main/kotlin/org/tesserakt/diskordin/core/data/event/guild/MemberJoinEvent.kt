package org.tesserakt.diskordin.core.data.event.guild

import arrow.core.ForId
import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.identifyId
import org.tesserakt.diskordin.core.data.invoke
import org.tesserakt.diskordin.core.data.json.response.JoinMemberResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.cache
import org.tesserakt.diskordin.core.entity.client

class MemberJoinEvent(raw: JoinMemberResponse) : IMemberEvent<ForId, ForIO> {
    override val member = raw.user.id.identifyId { raw.unwrap() }
    override val guild = raw.guildId.identify<IGuild> { client.getGuild(it) }

    init {
        cache[member.id] = member()
    }
}