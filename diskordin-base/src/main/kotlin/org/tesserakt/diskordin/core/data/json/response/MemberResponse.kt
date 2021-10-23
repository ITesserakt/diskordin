package org.tesserakt.diskordin.core.data.json.response

import kotlinx.datetime.Instant
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IMember
import org.tesserakt.diskordin.core.entity.IUser

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