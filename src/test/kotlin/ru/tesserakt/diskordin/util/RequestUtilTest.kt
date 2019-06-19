package ru.tesserakt.diskordin.util

import io.ktor.client.request.forms.formData
import io.ktor.http.HeadersBuilder
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class RequestUtilTest {
    @Test
    fun `Headers should contain only non-null values`() {
        val headers = HeadersBuilder() //Arrange

        headers.append("Test", null)
        headers.append("Test2", "message") //Act

        assertTrue(headers.contains("Test2"))
        assertFalse(headers.contains("Test")) //Assert
    }

    @Test
    fun `Forms should contain only non-null values`() {
        val data = formData {
            appendNullable("T", "s")
            appendNullable("T2", null)
        } //Arrange & Act

        assertTrue(data.isNotEmpty() && data.size == 1) //Assert
    }
}