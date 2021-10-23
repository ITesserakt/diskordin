package org.tesserakt.diskordin.core.entity.query

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@Suppress("NOTHING_TO_INLINE", "unused")
class BanQuery : IQuery {
    @ExperimentalTime
    private var deleteMessageDays: Duration? = null

    @ExperimentalTime
    operator fun Duration.unaryPlus() {
        deleteMessageDays = this
    }

    @ExperimentalTime
    inline fun BanQuery.deleteAfter(days: Duration) = days

    @ExperimentalTime
    override fun create() = mapOf(
        "delete_message_days" to deleteMessageDays?.toDouble(DurationUnit.DAYS).toString()
    )
}
