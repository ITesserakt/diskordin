package org.tesserakt.diskordin.core.data

import arrow.core.ForId
import arrow.core.extensions.id.applicative.just
import arrow.core.extensions.id.comonad.extract
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.RepeatedTest
import org.tesserakt.diskordin.core.entity.IMentioned
import kotlin.random.Random

internal class IdentifiedTest {
    private lateinit var sample: IdentifiedF<ForId, IMentioned>
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

        sample = snowflake identify { mock.just() }
    }

    @RepeatedTest(10)
    fun `invoke should return same id as in start`() {
        val data = sample().extract()
        data.id shouldEqual snowflake
    }
}