package org.tesserakt.diskordin.core.entity.query

import org.tesserakt.diskordin.core.data.Snowflake

@Suppress("NOTHING_TO_INLINE", "unused", "EXTENSION_SHADOWED_BY_MEMBER")
class UserGuildsQuery : IQuery {
    private var before: Snowflake? = null
    private var after: Snowflake? = null
    private var limit: Int = 100

    operator fun Before.unaryPlus() {
        before = this.v
    }

    operator fun After.unaryPlus() {
        after = this.v
    }

    operator fun Int.unaryPlus() {
        limit = this
    }

    inline fun UserGuildsQuery.before(id: Snowflake) = Before(id)
    inline fun UserGuildsQuery.after(id: Snowflake) = After(id)
    inline fun UserGuildsQuery.limit(value: Int = 100) = value

    override fun create(): Query = mapOf(
        "before" to before.toString(),
        "after" to after.toString(),
        "limit" to limit.toString()
    )
}
