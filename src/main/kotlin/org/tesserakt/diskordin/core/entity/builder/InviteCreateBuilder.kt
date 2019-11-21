package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.json.request.InviteCreateRequest
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@RequestBuilder
@Suppress("NOTHING_TO_INLINE", "unused")
class InviteCreateBuilder : AuditLogging<InviteCreateRequest>() {
    @ExperimentalTime
    override fun create(): InviteCreateRequest = InviteCreateRequest(
        maxAge.inSeconds.toInt(),
        maxUses,
        isTemporary,
        isUnique
    )

    @ExperimentalTime
    private var maxAge: Duration = 86400.seconds
    private var maxUses = 0
    private var isTemporary = false
    private var isUnique = false

    @ExperimentalTime
    operator fun Duration.unaryPlus() {
        maxAge = this
    }

    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    operator fun Int.unaryPlus() {
        maxUses = this
    }

    operator fun Temporary.unaryPlus() {
        isTemporary = this.v
    }

    operator fun Boolean.unaryPlus() {
        isUnique = this
    }

    @ExperimentalTime
    inline fun InviteCreateBuilder.maxAge(age: Duration = 86400.seconds) = age

    inline fun InviteCreateBuilder.maxUses(value: Int) = value
    inline fun InviteCreateBuilder.temporary(value: Boolean) = Temporary(value)
    inline fun InviteCreateBuilder.unique(value: Boolean) = value
}