package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.BulkDeleteRequest

class BulkDeleteBuilder : BuilderBase<BulkDeleteRequest>() {
    lateinit var messages: Array<Snowflake>

    override fun create(): BulkDeleteRequest = BulkDeleteRequest(
        messages.map { it.asLong() }.toTypedArray()
    )
}