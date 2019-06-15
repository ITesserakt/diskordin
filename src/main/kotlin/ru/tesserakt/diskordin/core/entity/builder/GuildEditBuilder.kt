package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.json.request.GuildEditRequest

class GuildEditBuilder : AuditLogging<GuildEditRequest>() {
    override fun create(): GuildEditRequest = GuildEditRequest(

    )

    override var reason: String? = null
}