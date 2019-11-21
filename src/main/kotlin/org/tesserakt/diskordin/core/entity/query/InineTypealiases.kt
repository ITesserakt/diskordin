package org.tesserakt.diskordin.core.entity.query

import org.tesserakt.diskordin.core.data.Snowflake

inline class Around(val v: Snowflake)
inline class Before(val v: Snowflake)
inline class After(val v: Snowflake)