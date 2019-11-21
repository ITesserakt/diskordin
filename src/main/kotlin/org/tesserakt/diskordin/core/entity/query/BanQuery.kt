package org.tesserakt.diskordin.core.entity.query

import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@Suppress("NOTHING_TO_INLINE", "unused")
@ExperimentalTime
class BanQuery : IQuery {
    private var deleteMessageDays: Duration? = null

    operator fun Duration.unaryPlus() {
        deleteMessageDays = this
    }

    inline fun BanQuery.deleteAfter(days: Duration) = days

    override fun create() = mapOf(
        "delete_message_days" to deleteMessageDays?.inDays.toString()
    )
}
