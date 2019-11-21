package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.Permission
import org.tesserakt.diskordin.core.data.json.request.PermissionsEditRequest
import org.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import org.tesserakt.diskordin.util.enums.ValuedEnum

@RequestBuilder
class PermissionEditBuilder(
    private val type: IPermissionOverwrite.Type,
    private val denied: ValuedEnum<Permission, Long>,
    private val allowed: ValuedEnum<Permission, Long>
) : AuditLogging<PermissionsEditRequest>() {
    override fun create(): PermissionsEditRequest = PermissionsEditRequest(
        allowed.code,
        denied.code,
        type.value
    )
}