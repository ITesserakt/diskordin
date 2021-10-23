package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.json.request.InviteCreateRequest
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@RequestBuilder
@Suppress("NOTHING_TO_INLINE", "unused")
class InviteCreateBuilder : AuditLogging<InviteCreateRequest>() {
    @ExperimentalTime
    override fun create(): InviteCreateRequest = InviteCreateRequest(
        maxAge.toDouble(DurationUnit.SECONDS).toInt(),
        maxUses,
        isTemporary,
        isUnique
    )

    @ExperimentalTime
    private var maxAge: Duration = Duration.seconds(86400)
    private var maxUses = 0
    private var isTemporary = false
    private var isUnique = false

    @ExperimentalTime
    operator fun Duration.unaryPlus() {
        maxAge = this
    }


    operator fun MaxUses.unaryPlus() {
        maxUses = this.v
    }

    operator fun Temporary.unaryPlus() {
        isTemporary = this.v
    }

    operator fun Boolean.unaryPlus() {
        isUnique = this
    }

    @ExperimentalTime
    inline fun InviteCreateBuilder.maxAge(age: Duration = Duration.seconds(86400)) = age

    inline fun InviteCreateBuilder.maxUses(value: Int) = MaxUses(value)
    inline fun InviteCreateBuilder.temporary(value: Boolean) = Temporary(value)
    inline fun InviteCreateBuilder.unique(value: Boolean) = value
}