package org.tesserakt.diskordin.core.entity.`object`

import arrow.core.Either
import org.tesserakt.diskordin.core.data.Permission
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IDiscordObject
import org.tesserakt.diskordin.util.enums.ValuedEnum

internal typealias RoleId = Snowflake
internal typealias MemberId = Snowflake

interface IPermissionOverwrite : IDiscordObject {
    val targetId: Either<RoleId, MemberId>
    val type: Type
    val allowed: ValuedEnum<Permission, Long>
    val denied: ValuedEnum<Permission, Long>

    enum class Type { Role, Member }

    fun computeCode(): Long
}