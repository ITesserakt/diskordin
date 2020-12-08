package org.tesserakt.diskordin.core.data.json.response

import kotlinx.datetime.Instant
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IMember
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.impl.core.entity.Member

sealed class MemberResponse<C : UnwrapContext>(
    val user: UserResponse<IUser>,
    val nick: String? = null,
    val roles: Array<Snowflake>,
    val joinedAt: Instant,
    val deaf: Boolean,
    val mute: Boolean
) : DiscordResponse<IMember, C>() {
    abstract fun copy(
        user: UserResponse<IUser> = this.user,
        nick: String? = this.nick,
        roles: Array<Snowflake> = this.roles,
        joinedAt: Instant = this.joinedAt,
        deaf: Boolean = this.deaf,
        mute: Boolean = this.mute
    ): MemberResponse<C>
}

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