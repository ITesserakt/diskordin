package ru.tesserakt.diskordin.core.entity.query

import ru.tesserakt.diskordin.core.data.Snowflake

class MemberQuery : IQuery {
    var limit = 1
    lateinit var after: Snowflake

    override fun create(): List<Pair<String, *>> = mapOf(
        "limit" to limit,
        "after" to if (::after.isInitialized) after.asLong() else 0
    ).toList()
}
