package ru.tesserakt.diskordin.util

import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test

internal class LoggersTest {
    private val log1 by Loggers
    private val log2 by Loggers

    companion object TestFixture {
        val log3 by Loggers
    }

    @Test
    fun `multiple call of Loggers should return the same value`() {
        log1 shouldEqual log2
    }

    @Test
    fun `call from companion class should return declaring class`() {
        log2 shouldEqual log3
    }
}