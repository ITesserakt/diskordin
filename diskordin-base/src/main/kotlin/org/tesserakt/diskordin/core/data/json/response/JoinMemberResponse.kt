package org.tesserakt.diskordin.core.data.json.response

import kotlinx.datetime.Instant
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IMember
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.impl.core.entity.Member

class JoinMemberResponse(
    user: UserResponse<IUser>,
    nick: String? = null,
    roles: Array<Snowflake>,
    joinedAt: Instant,
    deaf: Boolean,
    mute: Boolean,
    val guildId: Snowflake
) : MemberResponse<UnwrapContext.EmptyContext>(user, nick, roles, joinedAt, deaf, mute) {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): IMember = Member(this, guildId)
    override fun copy(
        user: UserResponse<IUser>,
        nick: String?,
        roles: Array<Snowflake>,
        joinedAt: Instant,
        deaf: Boolean,
        mute: Boolean
    ) = JoinMemberResponse(user, nick, roles, joinedAt, deaf, mute, guildId)
}