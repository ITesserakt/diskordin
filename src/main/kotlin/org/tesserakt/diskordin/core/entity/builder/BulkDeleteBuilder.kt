package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.BulkDeleteRequest

@Suppress("NOTHING_TO_INLINE", "unused")
@RequestBuilder
class BulkDeleteBuilder : BuilderBase<BulkDeleteRequest>() {
    private val messages = mutableListOf<Snowflake>()

    override fun create(): BulkDeleteRequest = BulkDeleteRequest(messages.toTypedArray())

    operator fun Snowflake.unaryPlus() {
        messages += this
    }

    operator fun Iterable<Snowflake>.unaryPlus() {
        messages += this
    }

    inline fun BulkDeleteBuilder.message(id: Snowflake) = id
    inline fun BulkDeleteBuilder.messages(ids: Iterable<Snowflake>) = ids
}