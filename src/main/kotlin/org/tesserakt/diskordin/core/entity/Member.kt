package org.tesserakt.diskordin.core.entity

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.builder.MemberEditBuilder

interface IMember : IUser, IEditable<IMember, MemberEditBuilder>, IGuildObject {
    val nickname: String?
    override val name: String
        get() = nickname ?: username

    val roles: Flow<IRole>

    val joinTime: Instant

    suspend fun addRole(role: IRole, reason: String?)
    suspend fun addRole(roleId: Snowflake, reason: String?)
    suspend fun removeRole(role: IRole, reason: String?)
    suspend fun removeRole(roleId: Snowflake, reason: String?)

    companion object : StaticMention<IMember, Companion> {
        override val mention: Regex = Regex(""""<@!(\d{18,})>"""")
    }
}