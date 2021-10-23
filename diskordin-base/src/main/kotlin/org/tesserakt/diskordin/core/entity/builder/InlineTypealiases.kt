package org.tesserakt.diskordin.core.entity.builder

import kotlinx.datetime.Instant
import org.tesserakt.diskordin.core.data.Snowflake

@JvmInline
value class Title(val v: String)

@JvmInline
value class Description(val v: String)

@JvmInline
value class URL(val v: String)

@JvmInline
value class Name(val v: String)

@JvmInline
value class IconURL(val v: String)

@JvmInline
value class Reason(val v: String)

@JvmInline
value class Position(val v: Int)

@JvmInline
value class Bitrate(val v: Int)

@JvmInline
value class Region(val v: String)

@JvmInline
value class AfkChannel(val v: Snowflake)

@JvmInline
value class Owner(val v: Snowflake)

@JvmInline
value class SplashURL(val v: String)

@JvmInline
value class SystemChannel(val v: Snowflake)

@JvmInline
value class Temporary(val v: Boolean)

@JvmInline
value class Muted(val v: Boolean)

@JvmInline
value class Role(val v: Snowflake)

@JvmInline
value class Hoisted(val v: Boolean)

@JvmInline
value class RateLimit(val v: Int)

@JvmInline
value class UserLimit(val v: Int)

@JvmInline
value class AfkTimeout(val v: Int)

@JvmInline
value class MaxUses(val v: Int)

@JvmInline
value class State(val v: String)

@JvmInline
value class Start(val v: Instant)

@JvmInline
value class Mentioned(val v: Boolean)