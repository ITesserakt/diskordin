package org.tesserakt.diskordin.core.data.event.guild

import arrow.core.Id
import arrow.core.extensions.id.comonad.comonad
import arrow.fx.ForIO
import arrow.fx.coroutines.stream.Chunk
import arrow.fx.coroutines.stream.Stream
import arrow.fx.coroutines.stream.filterOption
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.identifyId
import org.tesserakt.diskordin.core.data.invoke
import org.tesserakt.diskordin.core.data.json.response.GuildMemberResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.gateway.json.events.MemberUpdate
import org.tesserakt.diskordin.impl.core.entity.Member

class MemberUpdateEvent(raw: MemberUpdate) : IGuildEvent<ForIO> {
    override val guild = raw.guildId.identify<IGuild> { client.getGuild(it) }

    val roles = Stream.chunk(Chunk.array(raw.roles))
        .effectMap { guild().getRole(it) }
        .filterOption()

    val user = raw.user.id identifyId { raw.user.unwrap() }

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
