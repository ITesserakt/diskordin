@file:Suppress("unused", "DataClassPrivateConstructor", "EXPERIMENTAL_UNSIGNED_LITERALS", "NOTHING_TO_INLINE")

package org.tesserakt.diskordin.core.data

import arrow.core.Ordering
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Order
import arrow.typeclasses.Show
import kotlinx.datetime.Instant
import org.tesserakt.diskordin.core.data.Snowflake.ConstructionError.LessThenDiscordEpoch
import org.tesserakt.diskordin.core.data.Snowflake.ConstructionError.NotNumber

private const val DISCORD_EPOCH = 1420070400000u

@OptIn(ExperimentalUnsignedTypes::class)
data class Snowflake private constructor(internal val id: ULong) : Comparable<Snowflake> {
    override operator fun compareTo(other: Snowflake): Int = id.compareTo(other.id)
    override fun toString(): String = "$id"
    override fun hashCode(): Int = id.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (other !is Snowflake) return false

        return this.id == other.id
    }

    fun asString() = id.toString()
    fun asLong() = id.toLong()

    val timestamp: Instant
        get() = Instant.fromEpochMilliseconds(((id shr 22) + DISCORD_EPOCH).toLong())

    companion object {
        fun of(id: String): Snowflake = of(
            requireNotNull(id.toLongOrNull()) {
                "$id cannot be represented as Snowflake"
            })

        fun of(id: Long): Snowflake {
            require(id >= 0) { "id must be greater than 0" }
            return of(id.toULong())
        }

        fun of(id: ULong): Snowflake {
            require((id.toLong() shr 22) > 0) { "id must be greater than ${1 shl 22}" }
            return Snowflake(id)
        }
    }

    sealed class ConstructionError(val message: String) {
        object NotNumber : ConstructionError("Given string is not a number")
        object LessThenDiscordEpoch : ConstructionError("Given number is less than $DISCORD_EPOCH")
    }
}

fun String.asSnowflake() = Snowflake.of(this)

fun Long.asSnowflake() = Snowflake.of(this)

@ExperimentalUnsignedTypes
fun ULong.asSnowflake() = Snowflake.of(this)

@OptIn(ExperimentalUnsignedTypes::class)
fun <F> String.asSnowflakeSafe(AE: ApplicativeError<F, Snowflake.ConstructionError>) = AE.run {
    val stringToConvert = this@asSnowflakeSafe

    if (stringToConvert.toLongOrNull() == null)
        NotNumber.raiseError()
    else {
        val raw = toULongOrNull()
        if (raw == null || (raw.toLong() shr 22) <= 0)
            LessThenDiscordEpoch.raiseError()
        else asSnowflake().just()
    }
}

interface SnowflakeOrder : Order<Snowflake> {
    override fun Snowflake.compare(b: Snowflake): Ordering = Ordering.fromInt(compareTo(b))
}

inline fun Snowflake.Companion.order(): Order<Snowflake> = object : SnowflakeOrder {}

interface SnowflakeShow : Show<Snowflake> {
    override fun Snowflake.show(): String = asString()
}

inline fun Snowflake.Companion.show(): Show<Snowflake> = object : SnowflakeShow {}