package ru.tesserakt.diskordin.core.entity.builder

class BulkDeleteBuilder : BuilderBase<BulkDeleteRequest>() {
    lateinit var messages: Array<Snowflake>

    override fun create(): BulkDeleteRequest = BulkDeleteRequest(
        messages.map { it.asLong() }.toTypedArray()
    )
}