package org.tesserakt.diskordin.core.data

import arrow.fx.IO
import arrow.fx.extensions.fx
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.RepeatedTest
import org.tesserakt.diskordin.core.entity.IMentioned
import kotlin.random.Random

internal class IdentifiedTest {
    private lateinit var sample: Identified<IMentioned>
    private lateinit var snowflake: Snowflake

    @BeforeEach
    internal fun setUp() {
        snowflake = Random.nextLong(4194305, Long.MAX_VALUE).asSnowflake()
        println("Next id: $snowflake, ${snowflake.timestamp}")
        val mock = object : IMentioned {
            override val id: Snowflake = snowflake

            override val mention: String
                get() = throw NotImplementedError()
        }

        sample = snowflake identify { mock }
    }

    @RepeatedTest(10)
    fun `invoke should return same id as in start`() = IO.fx<Unit> {
        val data = sample().bind()
        data.id shouldEqual snowflake
    }.unsafeRunSync()
}