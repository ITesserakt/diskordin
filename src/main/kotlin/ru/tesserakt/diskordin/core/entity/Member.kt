package ru.tesserakt.diskordin.core.entity

import kotlinx.coroutines.flow.Flow
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.builder.MemberEditBuilder
import java.time.Instant

interface IMember : IUser, IGuildObject, IEditable<IMember, MemberEditBuilder> {
    val nickname: String?
    override val name: String
        get() = nickname ?: username

    val roles: Flow<IRole>

    val joinTime: Instant

    suspend fun addRole(role: IRole, reason: String?)
    suspend fun addRole(roleId: Snowflake, reason: String?)
    suspend fun removeRole(role: IRole, reason: String?)
    suspend fun removeRole(roleId: Snowflake, reason: String?)
}