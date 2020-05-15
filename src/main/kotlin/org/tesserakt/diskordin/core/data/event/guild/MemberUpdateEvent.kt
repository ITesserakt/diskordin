package org.tesserakt.diskordin.core.data.event.guild

import arrow.core.Id
import arrow.core.extensions.id.applicative.just
import arrow.core.extensions.id.comonad.comonad
import arrow.core.extensions.list.traverse.sequence
import arrow.core.extensions.listk.monadFilter.filterMap
import arrow.core.identity
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.applicative.map
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.invoke
import org.tesserakt.diskordin.core.data.json.response.GuildMemberResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IMember
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.cache
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.MemberUpdate
import org.tesserakt.diskordin.impl.core.entity.Member

class MemberUpdateEvent(raw: MemberUpdate) : IGuildEvent<ForIO> {
    override val guild = raw.guildId identify { client.getGuild(it) }

    val roles = raw.roles.map { id ->
        guild().map { it.getRole(id) }
    }.sequence(IO.applicative()).map { it.filterMap(::identity) }

    val user = raw.user.id identify { raw.user.unwrap().just() }

    val nick = raw.nick

    init {
        when (val cached = cache[user.id]) {
            is IMember -> { //if cached, just update fields
                val bean = GuildMemberResponse(
                    raw.user,
                    cached.nickname ?: nick,
                    raw.roles,
                    cached.joinTime,
                    deaf = false,
                    mute = false
                )
                cache[user.id] = Member(bean, guild.id)
            }
            null, is IUser -> cache[user.id] = user(Id.comonad())
        }
    }
}
