package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.json.request.InviteCreateRequest

class InviteCreateBuilder : AuditLogging<InviteCreateRequest>() {
    override fun create(): InviteCreateRequest = InviteCreateRequest(

    )

    override var reason: String? = null
}