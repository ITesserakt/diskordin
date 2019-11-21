package org.tesserakt.diskordin.impl.core.entity.`object`


import arrow.core.Either
import org.tesserakt.diskordin.core.data.Permission
import org.tesserakt.diskordin.core.data.json.response.OverwriteResponse
import org.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import org.tesserakt.diskordin.core.entity.`object`.MemberId
import org.tesserakt.diskordin.core.entity.`object`.RoleId
import org.tesserakt.diskordin.util.enums.ValuedEnum
import org.tesserakt.diskordin.util.enums.not
import org.tesserakt.diskordin.util.typeclass.integral

class PermissionOverwrite(raw: OverwriteResponse) : IPermissionOverwrite {
    override val type: IPermissionOverwrite.Type = IPermissionOverwrite.Type.of(raw.type)

    override val targetId: Either<RoleId, MemberId> = Either.cond(type == IPermissionOverwrite.Type.Role,
        { raw.id },
        { raw.id }
    )

    override val allowed = ValuedEnum<Permission, Long>(raw.allow, Long.integral())
    override val denied = ValuedEnum<Permission, Long>(raw.deny, Long.integral())

    override fun toString(): String {
        return StringBuilder("PermissionOverwrite(")
            .appendln("type=$type, ")
            .appendln("targetId=$targetId, ")
            .appendln("allowed=$allowed, ")
            .appendln("denied=$denied")
            .appendln(")")
            .toString()
    }

    override fun computeCode(): Long = (allowed and !denied).code
}