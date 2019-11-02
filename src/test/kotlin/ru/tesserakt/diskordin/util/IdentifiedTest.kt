package ru.tesserakt.diskordin.util

import arrow.fx.IO
import arrow.fx.extensions.fx
import org.amshove.kluent.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.RepeatedTest
import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.identify
import ru.tesserakt.diskordin.core.entity.IMentioned
import kotlin.random.Random

internal class IdentifiedTest {
    private lateinit var sample: Identified<IMentioned>
    private lateinit var snowflake: Snowflake

    @BeforeEach
    internal fun setUp() {
        snowflake = Random.nextLong(4194305, Long.MAX_VALUE).asSnowflake()
        println("Next id: $snowflake, ${snowflake.timestamp}")
        val mock = mock<IMentioned>()
        When calling mock.id `it returns` snowflake

        sample = snowflake identify { mock }
    }

    @RepeatedTest(10)
    fun `invoke should return same id as in start`() = IO.fx<Unit> {
        val data = sample().bind()
        data.id shouldEqual snowflake
    }.unsafeRunSync()
}