package org.tesserakt.diskordin.impl.core.client

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.checkAll
import org.tesserakt.diskordin.core.data.asSnowflake
import org.tesserakt.diskordin.impl.core.client.VerificationError.*

class TokenVerificationTest : StringSpec() {
    private val partial = { token: String -> token.verify() }

    init {
        "Blank string should produce error" {
            checkAll(Arb.stringPattern(" *")) {
                partial(it).shouldBeLeft().shouldBe(BlankString)
            }
        }

        "String, that contain spaces should produce error" {
            checkAll(Arb.stringPattern("""\s\w[\s\w]*""")) {
                partial(it).shouldBeLeft().shouldBe(InvalidCharacters)
            }
        }

        "Token should contain 2 dots" {
            checkAll(Arb.stringPattern("""\w*\.\w*""")) {
                partial(it).shouldBeLeft().shouldBe(InvalidConstruction)
            }
        }

        "First part of the token should be compressed Base64 id" {
            checkAll(Arb.stringPattern("""[a-zA-Z]+\.\w*\.\w*""")) {
                partial(it).shouldBeLeft().shouldBe(CorruptedId)
            }

            checkAll(Arb.stringPattern("""NTQ3NDg5MTA3NTg1MDA3NjM2\.\w*\.\w*""")) {
                partial(it).shouldBeRight().shouldBe(547489107585007636.asSnowflake())
            }
        }
    }
}