package org.tesserakt.diskordin.core.entity.query

import kotlin.time.ExperimentalTime

@Suppress("NOTHING_TO_INLINE", "unused")
class PruneQuery : IQuery {
    private var days: Int = 0

    operator fun Days.unaryPlus() {
        days = this.v
    }

    @ExperimentalTime
    inline fun PruneQuery.days(value: Int) = Days(value)

    override fun create(): Query = mapOf(
        "days" to days.toString()
    )
}
