package ru.tesserakt.diskordin.util

import arrow.core.orNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.tesserakt.diskordin.util.ThrowingPolicy.Quiet
import ru.tesserakt.diskordin.util.ThrowingPolicy.Verbose
import kotlin.test.assertNull

internal class ThrowingPolicyTest {
    @Test
    fun `verbose policy should log to console and wrap an exception`() {
        val policy: ThrowingPolicy = Verbose //Arrange
        val actual = policy.handle(RuntimeException("Test")).orNull() // Act

        assertNull(actual) //Assert
    }

    @Test
    fun `quiet policy should only throw an exception`() {
        val policy: ThrowingPolicy = Quiet //Arrange

        assertThrows<RuntimeException> {
            policy.handle(RuntimeException("Throw"))
        } //Act & Assert
    }
}