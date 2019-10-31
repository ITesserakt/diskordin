package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.IMember
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.impl.core.entity.Member
import java.time.Instant

sealed class MemberResponse<C : UnwrapContext>(
    val user: UserResponse<IUser>,
    val nick: String? = null,
    val roles: Array<Long>,
    val joinedAt: Instant,
    val deaf: Boolean,
    val mute: Boolean
) : DiscordResponse<IMember, C>()

class JoinMemberResponse(
    user: UserResponse<IUser>,
    nick: String? = null,
    roles: Array<Long>,
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
    roles: Array<Long>,
    joinedAt: Instant,
    deaf: Boolean,
    mute: Boolean
) : MemberResponse<UnwrapContext.GuildContext>(user, nick, roles, joinedAt, deaf, mute) {
    override fun unwrap(ctx: UnwrapContext.GuildContext): IMember = Member(this, ctx.guildId)
}