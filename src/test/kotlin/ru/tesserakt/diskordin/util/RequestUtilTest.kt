package ru.tesserakt.diskordin.util

import io.ktor.client.request.forms.formData
import io.ktor.http.HeadersBuilder
import org.amshove.kluent.shouldBe
import org.junit.jupiter.api.Test

internal class RequestUtilTest {
    @Test
    fun `Headers should contain only non-null values`() {
        val headers = HeadersBuilder() //Arrange

        headers.append("Test", null)
        headers.append("Test2", "message") //Act

        "Test2" shouldBeIn headers
        "Test" shouldNotBeIn headers //Assert
    }

    @Test
    fun `Forms should contain only non-null values`() {
        val data = formData {
            appendNullable("T", "s")
            appendNullable("T2", null)
        } //Arrange & Act

        (data.isNotEmpty() && data.size == 1) shouldBe true //Assert
    }
}

private infix fun String.shouldNotBeIn(headers: HeadersBuilder) = (this !in headers) shouldBe true

private infix fun String.shouldBeIn(headers: HeadersBuilder) = (this in headers) shouldBe true
