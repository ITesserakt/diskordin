package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.json.request.GuildRoleEditRequest
import org.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import java.awt.Color

class RoleEditBuilder : AuditLogging<GuildRoleEditRequest>() {
    override fun create(): GuildRoleEditRequest = GuildRoleEditRequest(
        name, permissions?.let { it.allowed or it.denied }?.code, color?.rgb, isHoisted, isMentionable
    )

    var name: String? = null
    var permissions: IPermissionOverwrite? = null
    var color: Color? = null
    var isHoisted: Boolean? = null
    var isMentionable: Boolean? = null
    override var reason: String? = null
}