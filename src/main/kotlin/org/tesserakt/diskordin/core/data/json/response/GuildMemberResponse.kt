package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IMember
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.impl.core.entity.Member
import java.time.Instant

sealed class MemberResponse<C : UnwrapContext>(
    val user: UserResponse<IUser>,
    val nick: String? = null,
    val roles: Array<Snowflake>,
    val joinedAt: Instant,
    val deaf: Boolean,
    val mute: Boolean
) : DiscordResponse<IMember, C>()

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
}

class GuildMemberResponse(
    user: UserResponse<IUser>,
    nick: String? = null,
    roles: Array<Snowflake>,
    joinedAt: Instant,
    deaf: Boolean,
    mute: Boolean
) : MemberResponse<UnwrapContext.GuildContext>(user, nick, roles, joinedAt, deaf, mute) {
    override fun unwrap(ctx: UnwrapContext.GuildContext): IMember = Member(this, ctx.guildId)
}