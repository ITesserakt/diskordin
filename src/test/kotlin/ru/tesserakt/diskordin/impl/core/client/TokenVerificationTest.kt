package ru.tesserakt.diskordin.impl.core.client

import arrow.core.Either
import arrow.core.EitherOf
import arrow.core.extensions.either.monadError.monadError
import arrow.core.fix
import arrow.core.orNull
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test
import ru.tesserakt.diskordin.core.client.TokenType
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.impl.core.client.TokenVerification.VerificationError.*

fun <E, A> EitherOf<E, A>.getLeft(): E {
    require(this is Either.Left<E>) { "Cannot get left value from Right Either" }
    return swap().orNull()!!
}

internal class TokenVerificationTest {
    val partial = { token: String ->
        TokenVerification(token, TokenType.Bot, Either.monadError())
    }

    @Test
    fun `blank string should cause error`() {
        val verification = partial("          ").verify()
        BlankString shouldEqual verification.getLeft()
    }

    @Test
    fun `spaces are not allowed too`() {
        val verification = partial("         test        ").verify()
        InvalidCharacters shouldEqual verification.getLeft()
    }

    @Test
    fun `token must contains 2 dots`() {
        val verification = partial("test.you").verify()
        InvalidConstruction shouldEqual verification.getLeft()
    }

    @Test
    fun `first part of the token should be right id`() {
        val verification = partial("some.incorrect.token").verify()
        CorruptedId shouldEqual verification.getLeft()
    }

    @Test
    fun `right token`() {
        val verification = partial("NTQ3NDg5MTA3NTg1MDA3NjM2.XQq07A.0POl52ji2E4lFlvf9HzdOw-Aisw")
            .verify().fix()

        verification.isRight() shouldBe true
        547489107585007636.asSnowflake() shouldEqual verification.orNull()
    }
}