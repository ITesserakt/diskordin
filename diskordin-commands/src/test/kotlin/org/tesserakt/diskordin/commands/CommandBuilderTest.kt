package org.tesserakt.diskordin.commands

import arrow.core.NonEmptyList
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.nel.forExactly
import io.kotest.assertions.arrow.nel.shouldHaveSize
import io.kotest.assertions.arrow.validation.shouldBeInvalid
import io.kotest.assertions.arrow.validation.shouldBeValid
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.shouldBe
import org.tesserakt.diskordin.commands.feature.AliasesFeature
import org.tesserakt.diskordin.commands.feature.DescriptionFeature
import org.tesserakt.diskordin.commands.feature.HiddenFeature

class CommandBuilderTest : FunSpec({
    test("Valid command builder should not produce errors") {
        val command = CommandBuilder().apply {
            +name("Test")
            +features(
                setOf(
                    DescriptionFeature("Test", "test"),
                    AliasesFeature("Test", listOf("help", "wanted"))
                )
            )
        }

        val validated = command.validate()

        validated shouldBeValid { (it: CommandObject) ->
            it.name shouldBe "Test"
            it.hasFeature<HiddenFeature>().shouldBeFalse()
            it.requiredFeatures shouldContainExactly setOf(
                DescriptionFeature("Test", "test"),
                AliasesFeature("Test", listOf("help", "wanted"))
            )
        }
    }

    context("Invalid command builder") {
        val command = CommandBuilder().apply {
            +features(
                setOf(
                    AliasesFeature("", listOf("Foo", "Bar", "Baz", "Foo"))
                )
            )
        }

        test("Errors should accumulate with Validated") {
            val validated = command.validate()

            validated shouldBeInvalid { (nel: NonEmptyList<ValidationError>) ->
                nel shouldHaveSize 2
                nel.toList() shouldHaveSingleElement { it is ValidationError.BlankName }
                nel.toList() shouldHaveSingleElement { it is AliasesFeature.DuplicatedAliases }
            }
        }

        test("Fail fast with Either") {
            val validated = command.validate().toEither()

            validated shouldBeLeft { nel ->
                nel shouldHaveSize 1
                nel.forExactly(1) { it is ValidationError.BlankName }
            }
        }
    }
})