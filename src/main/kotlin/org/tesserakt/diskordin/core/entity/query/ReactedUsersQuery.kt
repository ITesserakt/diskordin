package org.tesserakt.diskordin.core.entity.query

import org.tesserakt.diskordin.core.data.Snowflake

@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "NOTHING_TO_INLINE", "unused")
class ReactedUsersQuery : IQuery {
    @Suppress("UNCHECKED_CAST")
    override fun create() = mapOf(
        "before" to before?.asString(),
        "after" to after?.asString(),
        "limit" to limit.toString()
    ).filterValues { it != null } as Query

    private var before: Snowflake? = null
    private var after: Snowflake? = null
    private var limit: Int = 50

    operator fun Before.unaryPlus() {
        before = this.v
    }

    operator fun After.unaryPlus() {
        after = this.v
    }

    operator fun Int.unaryPlus() {
        require(this in 1..100) { "Value must be in [1; 100) range" }
        limit = this
    }

    inline fun ReactedUsersQuery.before(id: Snowflake) = Before(id)
    inline fun ReactedUsersQuery.after(id: Snowflake) = After(id)
    inline fun ReactedUsersQuery.limit(value: Int = 50) = value
}
