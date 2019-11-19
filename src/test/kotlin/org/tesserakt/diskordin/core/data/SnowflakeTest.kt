package org.tesserakt.diskordin.core.data

import arrow.core.Either
import arrow.core.extensions.either.applicativeError.applicativeError
import arrow.core.left
import arrow.core.right
import org.amshove.kluent.*
import org.junit.jupiter.api.Test
import org.tesserakt.diskordin.core.data.Snowflake.ConstructionError.LessThenDiscordEpoch
import org.tesserakt.diskordin.core.data.Snowflake.ConstructionError.NotANumber

internal class SnowflakeTest {
    @Test
    fun `toString()`() {
        val snowflake = 100000000000.asSnowflake()
        "100000000000" `should be equal to` snowflake.toString()
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

    @Test
    fun `safe converts`() {
        val s1 = "invalid"
        val s2 = "6666666666666666"
        val s3 = "0"

        s1.asSnowflakeSafe(Either.applicativeError()) `should equal` NotANumber.left()
        s2.asSnowflakeSafe(Either.applicativeError()) `should equal` Snowflake.of(6666666666666666).right()
        s3.asSnowflakeSafe(Either.applicativeError()) `should equal` LessThenDiscordEpoch.left()
    }
}