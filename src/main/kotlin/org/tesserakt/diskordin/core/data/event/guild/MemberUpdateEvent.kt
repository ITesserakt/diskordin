package org.tesserakt.diskordin.core.data.event.guild

import arrow.core.extensions.id.applicative.just
import arrow.core.extensions.list.traverse.sequence
import arrow.core.extensions.listk.monadFilter.filterMap
import arrow.core.identity
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.applicative.map
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.GuildMemberResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.MemberUpdate
import org.tesserakt.diskordin.impl.core.entity.Member
import org.tesserakt.diskordin.rest.storage.GlobalMemberCache
import java.time.Instant

class MemberUpdateEvent(raw: MemberUpdate) : IEvent {
    val guild = raw.guildId identify { client.getGuild(it) }

    val roles = raw.roles.map { id ->
        guild().map { it.getRole(id) }
    }.sequence(IO.applicative()).map { it.filterMap(::identity) }

    val user = raw.user.id identify { raw.user.unwrap().just() }

    val nick = raw.nick

    init {
        val cached = GlobalMemberCache[guild.id to user.id]
        val bean = GuildMemberResponse(
            raw.user,
            nick,
            raw.roles,
            cached?.joinTime ?: Instant.now(),
            deaf = false,
            mute = false
        )
        GlobalMemberCache[guild.id to user.id] = Member(bean, guild.id)
    }
}
