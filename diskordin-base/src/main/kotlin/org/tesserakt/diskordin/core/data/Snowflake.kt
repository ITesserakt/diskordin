package org.tesserakt.diskordin.core.data

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import kotlinx.datetime.Instant
import org.tesserakt.diskordin.core.data.Snowflake.ConstructionError.LessThenDiscordEpoch
import org.tesserakt.diskordin.core.data.Snowflake.ConstructionError.NotNumber

@Suppress("EXPERIMENTAL_UNSIGNED_LITERALS")
private const val DISCORD_EPOCH = 1420070400000u

@OptIn(ExperimentalUnsignedTypes::class)
inline class Snowflake(internal val id: ULong) : Comparable<Snowflake> {
    override fun toString(): String = "$id"
    override operator fun compareTo(other: Snowflake): Int = id.compareTo(other.id)

    sealed class ConstructionError(val message: String) {
        object NotNumber : ConstructionError("Given string is not a number")
        object LessThenDiscordEpoch : ConstructionError("Given number is less than $DISCORD_EPOCH")
    }
}

@Suppress("unused")
@OptIn(ExperimentalUnsignedTypes::class)
val Snowflake.timestamp
    get() = Instant.fromEpochMilliseconds(((id shr 22) + DISCORD_EPOCH).toLong())

@OptIn(ExperimentalUnsignedTypes::class)
fun String.asSnowflake(): Snowflake {
    val id = requireNotNull(this.toLongOrNull()) { "$this cannot be represented as Snowflake" }
    require(id >= 0) { "id must be greater than 0" }
    require((id shr 22) > 0) { "id must be greater than ${1 shl 22}" }
    return Snowflake(id.toULong())
}

@OptIn(ExperimentalUnsignedTypes::class)
fun Long.asSnowflake(): Snowflake {
    require(this >= 0) { "id must be greater than 0" }
    require((this shr 22) > 0) { "id must be greater than ${1 shl 22}" }
    return Snowflake(toULong())
}

@Suppress("unused")
@ExperimentalUnsignedTypes
fun ULong.asSnowflake(): Snowflake {
    require((this.toLong() shr 22) > 0) { "id must be greater than ${1 shl 22}" }
    return Snowflake(this)
}

@OptIn(ExperimentalUnsignedTypes::class)
fun String.asSnowflakeEither(): Either<Snowflake.ConstructionError, Snowflake> {
    val stringToConvert = this@asSnowflakeEither

    if (stringToConvert.toLongOrNull() == null)
        return NotNumber.left()
    val raw = toULongOrNull()
    if (raw == null || (raw.toLong() shr 22) <= 0)
        return LessThenDiscordEpoch.left()

    return asSnowflake().right()
}