package org.tesserakt.diskordin.core.entity.query

import org.tesserakt.diskordin.core.data.Snowflake

@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "NOTHING_TO_INLINE", "unused", "UNCHECKED_CAST")
class MemberQuery : IQuery {
    private var limit = 1
    private var after: Snowflake? = null

    operator fun Int.unaryPlus() {
        limit = this
    }

    operator fun Snowflake.unaryPlus() {
        after = this
    }

    inline fun MemberQuery.limit(value: Int = 1) = value
    inline fun MemberQuery.after(id: Snowflake) = id

    override fun create() = mapOf(
        "limit" to limit.toString(),
        "after" to after?.asString()
    ).filterValues { it != null } as Query
}
