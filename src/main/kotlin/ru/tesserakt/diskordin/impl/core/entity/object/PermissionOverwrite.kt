package ru.tesserakt.diskordin.impl.core.entity.`object`

import arrow.core.Either
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.Permission
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.computePermissions
import ru.tesserakt.diskordin.core.data.json.response.OverwriteResponse
import ru.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import ru.tesserakt.diskordin.core.entity.`object`.MemberId
import ru.tesserakt.diskordin.core.entity.`object`.RoleId
import java.util.*

class PermissionOverwrite(raw: OverwriteResponse, override val kodein: Kodein) : IPermissionOverwrite {
    override val type: IPermissionOverwrite.Type = IPermissionOverwrite.Type.of(raw.type)

    override val targetId: Either<RoleId, MemberId> = Either.cond(type == IPermissionOverwrite.Type.Role,
        { raw.id.asSnowflake() },
        { raw.id.asSnowflake() }
    )

    @ExperimentalUnsignedTypes
    override val allowed: EnumSet<Permission> = raw.allow.computePermissions()
    @ExperimentalUnsignedTypes
    override val denied: EnumSet<Permission> = raw.deny.computePermissions()

    override val client: IDiscordClient by instance()
}