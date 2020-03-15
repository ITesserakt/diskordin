package org.tesserakt.diskordin.impl.core.client

import arrow.core.Either
import arrow.core.extensions.either.monadError.monadError
import arrow.core.fix
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.property.arbitrary.Arb
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.arbitrary.take
import org.tesserakt.diskordin.core.data.asSnowflake
import org.tesserakt.diskordin.impl.core.client.VerificationError.*

class TokenVerificationTest : StringSpec() {
    private val partial = { token: String ->
        token.verify(Either.monadError()).fix()
    }

    private val testCount = System.getenv("test_count")?.toInt() ?: 1000

    init {
        "Blank string should produce error" {
            partial("   ") shouldBeLeft BlankString
        }

        "String, that contain spaces should produce error" {
            Arb.stringPattern(Regex("""\s\w[\s\w]*""").pattern).take(testCount).forAll {
                partial(it) shouldBeLeft InvalidCharacters
            }
        }

        "Token should contain 2 dots" {
            Arb.stringPattern(Regex("""\w*\.\w*""").pattern).take(testCount).forAll {
                partial(it) shouldBeLeft InvalidConstruction
            }
        }

        "First part of the token should be compressed Base64 id" {
            Arb.stringPattern(Regex("""[a-zA-Z]+\.\w*\.\w*""").pattern).take(testCount).forAll {
                partial(it) shouldBeLeft CorruptedId
            }

            Arb.stringPattern(Regex("""NTQ3NDg5MTA3NTg1MDA3NjM2\.\w*\.\w*""").pattern).take(testCount).forAll {
                partial(it) shouldBeRight 547489107585007636.asSnowflake()
            }
        }
    }
}