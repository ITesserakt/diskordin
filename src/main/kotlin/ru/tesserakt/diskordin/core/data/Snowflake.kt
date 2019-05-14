package ru.tesserakt.diskordin.core.data

import arrow.typeclasses.Order
import java.time.Instant

private const val DISCORD_EPOCH = 1420070400000

@UseExperimental(ExperimentalUnsignedTypes::class)
class Snowflake private constructor(private val id: ULong) : Order<Snowflake>, Comparable<Snowflake> {
    override fun compareTo(other: Snowflake): Int = id.compareTo(other.id)
    override fun Snowflake.compare(b: Snowflake): Int = id.compareTo(b.id)
    override fun toString(): String = id.toString()

    fun asString() = toString()
    fun asLong() = id.toLong()

    val timestamp: Instant
        get() = Instant.ofEpochMilli((id.toLong() shr 22) + DISCORD_EPOCH)

    companion object {
        fun of(id: String): Snowflake = of(
            requireNotNull(id.toULongOrNull()) {
                "$id cannot be represented as Snowflake"
            })

        fun of(id: Long): Snowflake = of(id.toULong())

        fun of(id: ULong): Snowflake {
            require((id.toLong() shr 22) > 0) { "Hey, it`s not a valid snowflake" }
            return Snowflake(id)
        }
    }
}


fun String.asSnowflake() = Snowflake.of(this)

fun Long.asSnowflake() = Snowflake.of(this)

fun ULong.asSnowflake() = Snowflake.of(this)