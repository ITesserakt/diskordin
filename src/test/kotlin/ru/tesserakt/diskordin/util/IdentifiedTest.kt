package ru.tesserakt.diskordin.util

import kotlinx.coroutines.runBlocking
import org.amshove.kluent.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.RepeatedTest
import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
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

        sample = Identified(snowflake) { mock }
    }

    @RepeatedTest(10)
    fun `invoke should return same id as in start`() = runBlocking<Unit> {
        val data = sample()
        data.id shouldEqual snowflake
    }

    @RepeatedTest(10)
    fun `destructor should contain id and data`() = runBlocking<Unit> {
        val (id, data) = sample
        id shouldEqual data.id
    }

    @RepeatedTest(10)
    fun `update returns new object with new id`() {
        val updated = sample.update(5000000000.asSnowflake()).update(snowflake)
        sample.id shouldEqual updated.id
        sample `should not equal` updated
    }
}