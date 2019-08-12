package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.Permission
import ru.tesserakt.diskordin.core.data.json.request.PermissionsEditRequest
import ru.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import ru.tesserakt.diskordin.util.enums.ValuedEnum

class PermissionEditBuilder : AuditLogging<PermissionsEditRequest>() {
    lateinit var allowed: ValuedEnum<Permission>
    lateinit var denied: ValuedEnum<Permission>
    lateinit var type: IPermissionOverwrite.Type

    override fun create(): PermissionsEditRequest = PermissionsEditRequest(
        allowed.code,
        denied.code,
        type.value
    )

    override var reason: String? = null
}