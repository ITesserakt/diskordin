package ru.tesserakt.diskordin.core.entity.query

import ru.tesserakt.diskordin.core.data.Snowflake

class MessagesQuery : IQuery {
    override fun create(): List<Pair<String, *>> = mapOf(
        "around" to around?.asLong(),
        "before" to before?.asLong(),
        "after" to after?.asLong(),
        "limit" to limit
    ).filterValues { it != null }.toList()

    var around: Snowflake? = null
    var before: Snowflake? = null
    var after: Snowflake? = null
    var limit: Long = 50
        set(value) {
            require(value in 1..1000) { "Value must be in [1; 1000) range" }
            field = value
        }
}