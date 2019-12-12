package org.tesserakt.diskordin.core.entity

import arrow.core.ListK
import arrow.fx.ForIO
import arrow.fx.IO
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.builder.MemberEditBuilder
import java.time.Instant

interface IMember : IUser, IEditable<IMember, MemberEditBuilder>, IGuildObject<ForIO> {
    val nickname: String?
    override val name: String
        get() = nickname ?: username

    val roles: IO<ListK<IRole>>

    val joinTime: Instant

    fun addRole(role: IRole, reason: String?): IO<Unit>
    fun addRole(roleId: Snowflake, reason: String?): IO<Unit>
    fun removeRole(role: IRole, reason: String?): IO<Unit>
    fun removeRole(roleId: Snowflake, reason: String?): IO<Unit>
}