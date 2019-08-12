package ru.tesserakt.diskordin.core.data

import org.amshove.kluent.*
import org.junit.jupiter.api.Test

internal class SnowflakeTest {
    @Test
    fun `toString()`() {
        val snowflake = 100000000000.asSnowflake()
        "Snowflake { 100000000000 }" `should be equal to` snowflake.toString()
    }

    @Test
    fun equality() {
        val snow = 100000000000.asSnowflake()
        val flake1 = 100000000000.asSnowflake()
        val flake2 = 500000000000.asSnowflake()

        snow shouldEqual flake1
        snow shouldNotEqual flake2
    }

    @Test
    @ExperimentalUnsignedTypes
    fun converts() {
        val s1 = "invalid"
        val s2 = "666666666666"
        val l1 = -12L
        val l2 = 999999999999L
        val ul1: ULong = 0u

        { s1.asSnowflake() } shouldThrow IllegalArgumentException::class withMessage "invalid cannot be represented as Snowflake"
        s2.asSnowflake() shouldEqual Snowflake.of(666666666666u);

        { l1.asSnowflake() } shouldThrow IllegalArgumentException::class withMessage "id must be greater then 0"
        l2.asSnowflake() shouldEqual Snowflake.of(999999999999u);

        { ul1.asSnowflake() } shouldThrow IllegalArgumentException::class withMessage "id must be greater then 4194304"
    }
}