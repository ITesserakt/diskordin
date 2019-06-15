package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.json.request.GuildRoleEditRequest

class RoleEditBuilder : AuditLogging<GuildRoleEditRequest>() {
    override fun create(): GuildRoleEditRequest = GuildRoleEditRequest(

    )

    override var reason: String? = null
}