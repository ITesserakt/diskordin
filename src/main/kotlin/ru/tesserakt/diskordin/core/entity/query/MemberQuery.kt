package ru.tesserakt.diskordin.core.entity.query

import ru.tesserakt.diskordin.core.data.Snowflake

class MemberQuery : IQuery {
    var limit = 1
    lateinit var after: Snowflake

    override fun create() = mapOf(
        "limit" to limit.toString(),
        "after" to after.asString()
    )
}
