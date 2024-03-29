package org.tesserakt.diskordin.core.data.json.response

import kotlinx.datetime.Instant
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IMember
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.impl.core.entity.Member

class GuildMemberResponse(
    user: UserResponse<IUser>,
    nick: String? = null,
    roles: Array<Snowflake>,
    joinedAt: Instant,
    deaf: Boolean,
    mute: Boolean
) : MemberResponse<UnwrapContext.GuildContext>(user, nick, roles, joinedAt, deaf, mute) {
    override fun unwrap(ctx: UnwrapContext.GuildContext): IMember = Member(this, ctx.guildId)
    override fun copy(
        user: UserResponse<IUser>,
        nick: String?,
        roles: Array<Snowflake>,
        joinedAt: Instant,
        deaf: Boolean,
        mute: Boolean
    ) = GuildMemberResponse(user, nick, roles, joinedAt, deaf, mute)
}