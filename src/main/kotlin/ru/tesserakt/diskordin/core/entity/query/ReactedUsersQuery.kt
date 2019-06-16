package ru.tesserakt.diskordin.core.entity.query

import ru.tesserakt.diskordin.core.data.Snowflake

class ReactedUsersQuery : IQuery {
    override fun create(): List<Pair<String, *>> = mapOf(
        "before" to before?.asLong(),
        "after" to after?.asLong(),
        "limit" to limit
    ).filterValues { it != null }.toList()

    var before: Snowflake? = null
    var after: Snowflake? = null
    var limit: Int = 50
        set(value) {
            require(value in 1..100) { "Value must be in [1; 100) range" }
            field = value
        }
}
