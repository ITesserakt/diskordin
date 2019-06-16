package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.computeCode
import ru.tesserakt.diskordin.core.data.json.request.GuildRoleEditRequest
import ru.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import ru.tesserakt.diskordin.util.plus
import java.awt.Color

class RoleEditBuilder : AuditLogging<GuildRoleEditRequest>() {
    override fun create(): GuildRoleEditRequest = GuildRoleEditRequest(
        name, permissions?.let { it.allowed + it.denied }?.computeCode(), color?.rgb, isHoisted, isMentionable
    )

    var name: String? = null
    var permissions: IPermissionOverwrite? = null
    var color: Color? = null
    var isHoisted: Boolean? = null
    var isMentionable: Boolean? = null
    override var reason: String? = null
}