package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.json.request.InviteCreateRequest
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

class InviteCreateBuilder : AuditLogging<InviteCreateRequest>() {
    @ExperimentalTime
    override fun create(): InviteCreateRequest = InviteCreateRequest(
        maxAge.inSeconds.toInt(),
        maxUses,
        isTemporary,
        isUnique
    )

    override var reason: String? = null
    @ExperimentalTime
    var maxAge: Duration = 86400.seconds
    var maxUses = 0
    var isTemporary = false
    var isUnique = false
}