package ru.tesserakt.diskordin.core.entity.`object`

import arrow.core.Either
import ru.tesserakt.diskordin.core.data.Permission
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.IDiscordObject
import ru.tesserakt.diskordin.util.enums.ValuedEnum

internal typealias RoleId = Snowflake
internal typealias MemberId = Snowflake

interface IPermissionOverwrite : IDiscordObject {
    val targetId: Either<RoleId, MemberId>
    val type: Type
    val allowed: ValuedEnum<Permission, Long>
    val denied: ValuedEnum<Permission, Long>

    enum class Type(internal val value: String) {
        Role("role"),
        Member("member");

        companion object {
            fun of(value: String) = when (value) {
                "role" -> Role
                "member" -> Member
                else -> throw NoSuchElementException()
            }
        }
    }
}