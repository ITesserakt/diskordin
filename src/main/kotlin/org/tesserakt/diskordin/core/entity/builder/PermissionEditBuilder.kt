package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.Permission
import org.tesserakt.diskordin.core.data.json.request.PermissionsEditRequest
import org.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import org.tesserakt.diskordin.util.enums.ValuedEnum

class PermissionEditBuilder : AuditLogging<PermissionsEditRequest>() {
    lateinit var allowed: ValuedEnum<Permission, Long>
    lateinit var denied: ValuedEnum<Permission, Long>
    lateinit var type: IPermissionOverwrite.Type

    override fun create(): PermissionsEditRequest = PermissionsEditRequest(
        allowed.code,
        denied.code,
        type.value
    )

    override var reason: String? = null
}