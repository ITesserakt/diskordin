package ru.tesserakt.diskordin.impl.core.client

import arrow.core.Validated
import arrow.core.orNull
import org.amshove.kluent.`should be`
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.junit.jupiter.api.Test
import ru.tesserakt.diskordin.core.client.TokenType
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.impl.core.client.TokenVerification.VerificationError.*

fun <E, A> Validated<E, A>.getLeft(): E = swap().orNull()!!

internal class TokenVerificationTest {
    val partial = { token: String ->
        TokenVerification(token, TokenType.Bot)
    }

    @Test
    fun `blank string should cause error`() {
        val verification = partial("          ").verify()
        verification.isInvalid `should be` true
        BlankString shouldEqual verification.getLeft()
    }

    @Test
    fun `spaces are not allowed too`() {
        val verification = partial("         test        ").verify()

        verification.isInvalid shouldBe true
        InvalidCharacters shouldEqual verification.getLeft()
    }

    @Test
    fun `token must contains 2 dots`() {
        val verification = partial("test.you").verify()

        verification.isInvalid shouldBe true
        InvalidConstruction shouldEqual verification.getLeft()
    }

    @Test
    fun `first part of the token should be right id`() {
        val verification = partial("some.incorrect.token");

        { verification.verify() } shouldThrow IllegalArgumentException::class
    }

    @Test
    fun `right token`() {
        val verification = partial("NTQ3NDg5MTA3NTg1MDA3NjM2.XQq07A.0POl52ji2E4lFlvf9HzdOw-Aisw")
            .verify()

        verification.isValid shouldBe true
        547489107585007636.asSnowflake() shouldEqual verification.orNull()
    }
}