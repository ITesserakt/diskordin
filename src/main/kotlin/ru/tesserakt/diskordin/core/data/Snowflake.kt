@file:Suppress("unused", "DataClassPrivateConstructor")

package ru.tesserakt.diskordin.core.data

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.extension
import arrow.typeclasses.Order
import arrow.typeclasses.Show
import ru.tesserakt.diskordin.core.data.Snowflake.ConstructionError.LessThenDiscordEpoch
import ru.tesserakt.diskordin.core.data.Snowflake.ConstructionError.NotANumber
import java.time.Instant

private const val DISCORD_EPOCH = 1420070400000

@UseExperimental(ExperimentalUnsignedTypes::class)
data class Snowflake private constructor(private val id: ULong) : Comparable<Snowflake> {
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
        get() = Instant.ofEpochMilli((id.toLong() shr 22) + DISCORD_EPOCH)

    companion object {
        fun of(id: String): Snowflake = of(
            requireNotNull(id.toLongOrNull()) {
                "$id cannot be represented as Snowflake"
            })

        fun of(id: Long): Snowflake {
            require(id >= 0) { "id must be greater then 0" }
            return of(id.toULong())
        }

        fun of(id: ULong): Snowflake {
            require((id.toLong() shr 22) > 0) { "id must be greater then ${1 shl 22}" }
            return Snowflake(id)
        }
    }

    sealed class ConstructionError(val message: String) {
        object NotANumber : ConstructionError("Given string is not a number")
        object LessThenDiscordEpoch : ConstructionError("Given number is less then $DISCORD_EPOCH")
    }
}

fun String.asSnowflake() = Snowflake.of(this)

fun Long.asSnowflake() = Snowflake.of(this)

@ExperimentalUnsignedTypes
fun ULong.asSnowflake() = Snowflake.of(this)

@UseExperimental(ExperimentalUnsignedTypes::class)
fun String.asSnowflakeSafe(): Either<Snowflake.ConstructionError, Snowflake> {
    val stringToConvert = this@asSnowflakeSafe

    if (stringToConvert.toLongOrNull() == null)
        return NotANumber.left()

    val raw = toULongOrNull()
    if (raw == null || raw < DISCORD_EPOCH.toULong())
        return LessThenDiscordEpoch.left()

    return asSnowflake().right()
}

@extension
interface SnowflakeOrder : Order<Snowflake> {
    override fun Snowflake.compare(b: Snowflake): Int = this.compareTo(b)
}

interface SnowflakeShow : Show<Snowflake> {
    override fun Snowflake.show(): String = asString()
}