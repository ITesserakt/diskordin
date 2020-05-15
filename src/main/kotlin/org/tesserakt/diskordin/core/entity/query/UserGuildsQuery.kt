package org.tesserakt.diskordin.core.entity.query

import org.tesserakt.diskordin.core.data.Snowflake

@Suppress("NOTHING_TO_INLINE", "unused")
class UserGuildsQuery : IQuery {
    private var before: Snowflake? = null
    private var after: Snowflake? = null
    private var limit: Byte = 100

    operator fun Before.unaryPlus() {
        before = this.v
    }

    operator fun After.unaryPlus() {
        after = this.v
    }

    operator fun Limit.unaryPlus() {
        limit = this.v
    }

    inline fun UserGuildsQuery.before(id: Snowflake) = Before(id)
    inline fun UserGuildsQuery.after(id: Snowflake) = After(id)
    inline fun UserGuildsQuery.limit(value: Byte = 100) = Limit(value)

    override fun create(): Query = mapOf(
        "before" to before.toString(),
        "after" to after.toString(),
        "limit" to limit.toString()
    )
}
