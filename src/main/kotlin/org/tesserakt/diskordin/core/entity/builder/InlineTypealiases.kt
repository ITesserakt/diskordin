package org.tesserakt.diskordin.core.entity.builder

import kotlinx.datetime.Instant
import org.tesserakt.diskordin.core.data.Snowflake

inline class Title(val v: String)
inline class Description(val v: String)
inline class URL(val v: String)
inline class Name(val v: String)
inline class IconURL(val v: String)
inline class Reason(val v: String)
inline class Position(val v: Int)
inline class Bitrate(val v: Int)
inline class Region(val v: String)
inline class AfkChannel(val v: Snowflake)
inline class Owner(val v: Snowflake)
inline class SplashURL(val v: String)
inline class SystemChannel(val v: Snowflake)
inline class Temporary(val v: Boolean)
inline class Muted(val v: Boolean)
inline class Role(val v: Snowflake)
inline class Hoisted(val v: Boolean)
inline class RateLimit(val v: Int)
inline class UserLimit(val v: Int)
inline class AfkTimeout(val v: Int)
inline class MaxUses(val v: Int)
inline class State(val v: String)
inline class Start(val v: Instant)
inline class Mentioned(val v: Boolean)