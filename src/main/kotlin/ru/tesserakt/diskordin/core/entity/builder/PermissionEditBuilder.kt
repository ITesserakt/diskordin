package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.Permission
import ru.tesserakt.diskordin.core.data.computeCode
import ru.tesserakt.diskordin.core.data.json.request.PermissionsEditRequest
import ru.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import java.util.*

class PermissionEditBuilder : IAuditLogging<PermissionsEditRequest> {
    @ExperimentalUnsignedTypes
    lateinit var allowed: EnumSet<Permission>
    @ExperimentalUnsignedTypes
    lateinit var denied: EnumSet<Permission>
    lateinit var type: IPermissionOverwrite.Type


    @ExperimentalUnsignedTypes
    override fun create(): PermissionsEditRequest = PermissionsEditRequest(
        allowed.computeCode().toInt(),
        denied.computeCode().toInt(),
        type.value
    )

    override var reason: String? = null
}