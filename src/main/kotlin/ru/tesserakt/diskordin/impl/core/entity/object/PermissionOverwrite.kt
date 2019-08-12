package ru.tesserakt.diskordin.impl.core.entity.`object`


import arrow.core.Either
import ru.tesserakt.diskordin.core.data.Permission
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.OverwriteResponse
import ru.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import ru.tesserakt.diskordin.core.entity.`object`.MemberId
import ru.tesserakt.diskordin.core.entity.`object`.RoleId
import ru.tesserakt.diskordin.util.enums.ValuedEnum

class PermissionOverwrite(raw: OverwriteResponse) : IPermissionOverwrite {
    override val type: IPermissionOverwrite.Type = IPermissionOverwrite.Type.of(raw.type)

    override val targetId: Either<RoleId, MemberId> = Either.cond(type == IPermissionOverwrite.Type.Role,
        { raw.id.asSnowflake() },
        { raw.id.asSnowflake() }
    )

    @ExperimentalUnsignedTypes
    override val allowed = ValuedEnum<Permission>(raw.allow)
    @ExperimentalUnsignedTypes
    override val denied = ValuedEnum<Permission>(raw.deny)


}