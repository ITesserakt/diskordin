package ru.tesserakt.diskordin.util

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.entity.IMentioned
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

internal class IdentifiedTest {
    lateinit var sample: Identified<IMentioned>
    private val snowflake = 1049034030030.asSnowflake()

    @BeforeEach
    internal fun setUp() {
        sample = Identified(snowflake) { id ->
            mockk<IMentioned> {
                every { this@mockk.id } returns id
            }
        }
    }

    @Test
    fun `invoke should return same id as in start`() = runBlocking {
        val data = sample()
        assertEquals(data.id, snowflake)
    }

    @Test
    fun `destructor should contain id and data`() = runBlocking {
        val (id, data) = sample
        assertEquals(id, data.id)
    }

    @Test
    fun `update returns new object with new id`() {
        val updated = sample.update(5000000000.asSnowflake()).update(snowflake)
        assertSame(sample.id, updated.id)
        assertNotEquals(sample, updated)
    }
}