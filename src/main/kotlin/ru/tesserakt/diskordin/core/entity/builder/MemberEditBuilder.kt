package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.json.request.MemberEditRequest

class MemberEditBuilder : AuditLogging<MemberEditRequest>() {
    override fun create(): MemberEditRequest = MemberEditRequest(

    )

    override var reason: String? = null
}