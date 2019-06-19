package ru.tesserakt.diskordin.util

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class LoggersTest {
    private val log1 by Loggers
    private val log2 by Loggers

    companion object TestFixture {
        val log3 by Loggers
    }

    @Test
    fun `multiple call of Loggers should return the same value`() {
        assertEquals(log1, log2)
    }

    @Test
    fun `call from companion class should return declaring class`() {
        assertEquals(log2, log3)
    }
}