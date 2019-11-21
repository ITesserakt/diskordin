package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.json.request.GuildRoleCreateRequest
import java.awt.Color
import kotlin.properties.Delegates

@RequestBuilder
class RoleCreateBuilder(val name: String, val color: Color) : AuditLogging<GuildRoleCreateRequest>() {
    var permissions: Int by Delegates.notNull()
    var isHoisted: Boolean by Delegates.notNull()
    var isMentionable: Boolean by Delegates.notNull()

    override fun create(): GuildRoleCreateRequest = GuildRoleCreateRequest(
        name,
        permissions,
        color.rgb,
        isHoisted,
        isMentionable
    )
}
