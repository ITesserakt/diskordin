package org.tesserakt.diskordin.impl.core.client

import arrow.core.Either
import arrow.core.extensions.either.monadError.monadError
import arrow.core.fix
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.tesserakt.diskordin.core.data.asSnowflake
import org.tesserakt.diskordin.getLeft
import org.tesserakt.diskordin.getRight
import org.tesserakt.diskordin.impl.core.client.VerificationError.*

internal class TokenVerificationTest {
    val partial = { token: String ->
        token.verify(Either.monadError()).fix()
    }

    @Test
    fun `blank string should cause error`() {
        val verification = partial("          ")
        BlankString shouldBeEqualTo verification.getLeft()
    }

    @Test
    fun `spaces are not allowed too`() {
        val verification = partial("         test        ")
        InvalidCharacters shouldBeEqualTo verification.getLeft()
    }

    @Test
    fun `token must contains 2 dots`() {
        val verification = partial("test.you")
        InvalidConstruction shouldBeEqualTo verification.getLeft()
    }

    @Test
    fun `first part of the token should be right id`() {
        val verification = partial("some.incorrect.token")
        InvalidCharacters shouldBeEqualTo verification.getLeft()
    }

    @Test
    fun `right token`() {
        val verification = partial("NTQ3NDg5MTA3NTg1MDA3NjM2.XQq07A.0POl52ji2E4lFlvf9HzdOw-Aisw")

        verification.isRight() shouldBe true
        547489107585007636.asSnowflake() shouldBeEqualTo verification.getRight()
    }
}