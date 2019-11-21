package org.tesserakt.diskordin.core.entity.query

import kotlin.time.ExperimentalTime

@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "NOTHING_TO_INLINE", "unused")
class PruneQuery : IQuery {
    private var days: Int = 0

    operator fun Int.unaryPlus() {
        days = this
    }

    @ExperimentalTime
    inline fun PruneQuery.days(value: Int) = value

    override fun create(): Query = mapOf(
        "days" to days.toString()
    )
}
