package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.Permissions
import org.tesserakt.diskordin.core.data.json.request.PermissionsEditRequest
import org.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite

@RequestBuilder
class PermissionEditBuilder(
    private val type: IPermissionOverwrite.Type,
    private val denied: Permissions,
    private val allowed: Permissions
) : AuditLogging<PermissionsEditRequest>() {
    override fun create(): PermissionsEditRequest = PermissionsEditRequest(
        allowed.code,
        denied.code,
        type.value
    )
}