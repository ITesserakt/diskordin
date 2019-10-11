package ru.tesserakt.diskordin.core.entity.query

import ru.tesserakt.diskordin.core.data.Snowflake

class ReactedUsersQuery : IQuery {
    @Suppress("UNCHECKED_CAST")
    override fun create() = mapOf(
        "before" to before?.asString(),
        "after" to after?.asString(),
        "limit" to limit.toString()
    ).filterValues { it != null } as Query

    var before: Snowflake? = null
    var after: Snowflake? = null
    var limit: Int = 50
        set(value) {
            require(value in 1..100) { "Value must be in [1; 100) range" }
            field = value
        }
}
