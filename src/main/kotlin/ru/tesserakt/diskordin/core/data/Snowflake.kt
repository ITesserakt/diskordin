@file:Suppress("unused")

package ru.tesserakt.diskordin.core.data

import java.time.Instant

private const val DISCORD_EPOCH = 1420070400000

@UseExperimental(ExperimentalUnsignedTypes::class)
class Snowflake private constructor(private val id: ULong) : Comparable<Snowflake> {
    override operator fun compareTo(other: Snowflake): Int = id.compareTo(other.id)
    override fun toString(): String = "Snowflake { $id }"

    fun asString() = id.toString()
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

@ExperimentalUnsignedTypes
fun ULong.asSnowflake() = Snowflake.of(this)