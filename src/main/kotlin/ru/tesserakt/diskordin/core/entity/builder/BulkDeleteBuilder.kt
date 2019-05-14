package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.request.BulkDeleteRequest

class BulkDeleteBuilder : IBuilder<BulkDeleteRequest> {

    lateinit var messages: Array<Snowflake>


    override fun create(): BulkDeleteRequest = BulkDeleteRequest(
        messages.map { it.asLong() }
            .toTypedArray()
    )
}