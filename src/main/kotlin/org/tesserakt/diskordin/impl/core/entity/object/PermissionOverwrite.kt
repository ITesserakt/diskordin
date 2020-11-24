package org.tesserakt.diskordin.impl.core.entity.`object`


import arrow.core.Either
import org.tesserakt.diskordin.core.data.Permission
import org.tesserakt.diskordin.core.data.json.response.OverwriteResponse
import org.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import org.tesserakt.diskordin.core.entity.`object`.MemberId
import org.tesserakt.diskordin.core.entity.`object`.RoleId
import org.tesserakt.diskordin.impl.util.typeclass.integral
import org.tesserakt.diskordin.util.enums.ValuedEnum
import org.tesserakt.diskordin.util.enums.and
import org.tesserakt.diskordin.util.enums.not

internal class PermissionOverwrite(raw: OverwriteResponse) : IPermissionOverwrite {
    override val type: IPermissionOverwrite.Type = IPermissionOverwrite.Type.of(raw.type)

    override val targetId: Either<RoleId, MemberId> = Either.cond(type == IPermissionOverwrite.Type.Role,
        { raw.id },
        { raw.id }
    )

    override val allowed = ValuedEnum<Permission, Long>(raw.allow, Long.integral())
    override val denied = ValuedEnum<Permission, Long>(raw.deny, Long.integral())

    override fun computeCode(): Long = (allowed and !denied).code

    override fun toString(): String {
        return "PermissionOverwrite(type=$type, targetId=$targetId, allowed=$allowed, denied=$denied)"
    }
}