package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.json.request.GuildRoleCreateRequest
import java.awt.Color
import kotlin.properties.Delegates

class RoleCreateBuilder : AuditLogging<GuildRoleCreateRequest>() {
    override var reason: String? = null
    lateinit var name: String
    var permissions: Int by Delegates.notNull()
    lateinit var color: Color
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
