package ru.tesserakt.diskordin.core.entity.query

import ru.tesserakt.diskordin.core.data.Snowflake

class UserGuildsQuery : IQuery {
    var before: Snowflake? = null
    var after: Snowflake? = null
    var limit: Int = 100

    override fun create(): Query = mapOf(
        "before" to before.toString(),
        "after" to after.toString(),
        "limit" to limit.toString()
    )
}
