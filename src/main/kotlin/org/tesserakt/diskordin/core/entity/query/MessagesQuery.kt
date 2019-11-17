package org.tesserakt.diskordin.core.entity.query

import org.tesserakt.diskordin.core.data.Snowflake

class MessagesQuery : IQuery {
    @Suppress("UNCHECKED_CAST")
    override fun create() = mapOf(
        "around" to around?.asString(),
        "before" to before?.asString(),
        "after" to after?.asString(),
        "limit" to limit.toString()
    ).filterValues { it != null } as Query

    var around: Snowflake? = null
    var before: Snowflake? = null
    var after: Snowflake? = null
    var limit: Long = 50
        set(value) {
            require(value in 1..100) { "Value must be in [1; 100) range" }
            field = value
        }
}