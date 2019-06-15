package ru.tesserakt.diskordin.core.entity.query

import ru.tesserakt.diskordin.core.data.Snowflake

class UserGuildsQuery : IQuery {
    var before: Snowflake? = null
    var after: Snowflake? = null
    var limit: Int = 100

    override fun create(): Query = mapOf(
        "before" to before,
        "after" to after,
        "limit" to limit
    ).filterValues { it != null }.toList()
}
