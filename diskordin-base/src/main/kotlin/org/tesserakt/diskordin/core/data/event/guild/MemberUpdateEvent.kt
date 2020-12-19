package org.tesserakt.diskordin.core.data.event.guild

import arrow.fx.ForIO
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapNotNull
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.identifyId
import org.tesserakt.diskordin.core.data.invoke
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.MemberUpdate

class MemberUpdateEvent(raw: MemberUpdate) : IGuildEvent<ForIO> {
    override val guild = raw.guildId.identify<IGuild> { client.getGuild(it) }

    val roles = flowOf(*raw.roles)
        .mapNotNull { guild().getRole(it) }

    val user = raw.user.id identifyId { raw.user.unwrap() }

    val nick = raw.nick
}
