package org.tesserakt.diskordin.core.entity.query

import org.tesserakt.diskordin.core.data.Snowflake

class MemberQuery : IQuery {
    var limit = 1
    var after: Snowflake? = null

    override fun create() = mapOf(
        "limit" to limit.toString(),
        "after" to after?.asString()
    ).filterValues { it != null } as Query
}
