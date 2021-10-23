package org.tesserakt.diskordin.core.entity.query

import org.tesserakt.diskordin.core.data.Snowflake

@JvmInline
value class Around(val v: Snowflake)

@JvmInline
value class Before(val v: Snowflake)

@JvmInline
value class After(val v: Snowflake)

@JvmInline
value class Limit(val v: Byte)

@JvmInline
value class Days(val v: Int)