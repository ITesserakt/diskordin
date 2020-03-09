package org.tesserakt.diskordin.commands

import arrow.Kind
import arrow.core.*
import arrow.core.extensions.either.traverse.traverse
import arrow.core.extensions.validated.traverse.traverse
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.nel.forExactly
import io.kotest.assertions.arrow.nel.shouldHaveSize
import io.kotest.assertions.arrow.validation.shouldBeInvalid
import io.kotest.assertions.arrow.validation.shouldBeValid
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class CommandBuilderTest : FunSpec({
    test("Valid command builder should not produce errors") {
        val command = CommandBuilder().apply {
            +name("Test")
            +description("Test command")
            +aliases("help", "wanted")
        }

        val validated: Kind<Kind<ForValidated, NonEmptyList<ValidationError>>, CommandObject> =
            CommandBuilder.Validator.accumulateErrors {
                command.validate(this, Validated.traverse())
            }

        validated.fix() shouldBeValid { (it: CommandObject) ->
            it.name shouldBe "Test"
            it.description shouldBe "Test command"
            it.aliases shouldBe listOf("help", "wanted")
            it.isHidden.shouldBeFalse()
            it.requiredFeatures shouldHaveSize 0
        }
    }

    context("Invalid command builder") {
        val command = CommandBuilder().apply {
            +name("Foo")
            +aliases("Foo", "Bar", "Baz", "Foo")
        }

        test("Errors should accumulate with Validated") {
            val validated: Kind<Kind<ForValidated, NonEmptyList<ValidationError>>, CommandObject> =
                CommandBuilder.Validator.accumulateErrors {
                    command.validate(this, Validated.traverse())
                }

            validated.fix() shouldBeInvalid { (nel: NonEmptyList<ValidationError>) ->
                println(nel)
                nel shouldHaveSize 2
                nel.toList() shouldHaveSingleElement { it is ValidationError.Empty }
                nel.toList() shouldHaveSingleElement { it is ValidationError.DuplicatedAliases }
            }
        }

        test("Fail fast with Either") {
            val validated: Kind<Kind<ForEither, NonEmptyList<ValidationError>>, CommandObject> =
                CommandBuilder.Validator.failFast {
                    command.validate(this, Either.traverse())
                }

            validated.fix() shouldBeLeft { nel ->
                nel shouldHaveSize 1
                nel.forExactly(1) { it is ValidationError.Empty }
            }
        }
    }
})