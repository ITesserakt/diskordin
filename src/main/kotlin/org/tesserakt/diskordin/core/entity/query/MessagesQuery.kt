package org.tesserakt.diskordin.core.entity.query

import org.tesserakt.diskordin.core.data.Snowflake

@Suppress("NOTHING_TO_INLINE", "unused")
class MessagesQuery : IQuery {
    @Suppress("UNCHECKED_CAST")
    override fun create() = mapOf(
        "around" to around?.asString(),
        "before" to before?.asString(),
        "after" to after?.asString(),
        "limit" to limit.toString()
    ).filterValues { it != null } as Query

    private var around: Snowflake? = null
    private var before: Snowflake? = null
    private var after: Snowflake? = null
    private var limit: Byte = 50

    operator fun Around.unaryPlus() {
        around = this.v
    }

    operator fun Before.unaryPlus() {
        before = this.v
    }

    operator fun After.unaryPlus() {
        after = this.v
    }

    operator fun Limit.unaryPlus() {
        require(this.v in 1..100) { "Value must be in [1; 100) range" }
        limit = this.v.toByte()
    }

    inline fun MessagesQuery.around(id: Snowflake) = Around(id)
    inline fun MessagesQuery.before(id: Snowflake) = Before(id)
    inline fun MessagesQuery.after(id: Snowflake) = After(id)
    inline fun MessagesQuery.limit(value: Byte = 50) = value
}