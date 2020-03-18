package org.tesserakt.diskordin.core.data

import arrow.core.Either
import arrow.core.extensions.either.applicativeError.applicativeError
import arrow.core.fix
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forSome
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.take
import io.kotest.property.exhaustive.azstring
import org.amshove.kluent.shouldNotBe
import org.tesserakt.diskordin.core.data.Snowflake.ConstructionError.LessThenDiscordEpoch
import org.tesserakt.diskordin.core.data.Snowflake.ConstructionError.NotNumber

private const val MIN_SNOWFLAKE = 4194305L
private val testCount = System.getenv("test_count")?.toInt() ?: 1000

class SnowflakeTest : FunSpec() {
    private val snowflakes
        get() = Arb.long(MIN_SNOWFLAKE)
            .take(testCount).filter { it >= MIN_SNOWFLAKE }
            .map { it.asSnowflake() }

    init {
        test("Snowflake.toString should return number in string") {
            snowflakes.forAll {
                it.toString() shouldBe "$it"
            }
        }

        test("Snowflake should not compare by references") {
            snowflakes.zipWithNext().forAll { (current, next) ->
                val copy = current.copy()

                current shouldNotBe next
                current shouldBe copy
                current shouldNotBeSameInstanceAs copy
                current shouldBeSameInstanceAs current
            }
        }

        context("Non-safe converts should throw errors comparing their rules") {
            test("String, that hasn't digits") {
                Exhaustive.azstring(1..30).generate(RandomSource.Default).map { it.value }.take(testCount).forAll {
                    shouldThrow<IllegalArgumentException> {
                        it.asSnowflake()
                    }.message shouldEndWith "cannot be represented as Snowflake"
                }
            }

            test("Number, that less then 0") {
                Arb.long().take(testCount).forSome {
                    shouldThrow<IllegalArgumentException> {
                        it.asSnowflake()
                    }.message shouldBe "id must be greater than 0"
                }
            }

            test("Number, that less then $MIN_SNOWFLAKE") {
                Arb.long(1, MIN_SNOWFLAKE).values(RandomSource.Default).map { it.value }.take(testCount).forAll {
                    shouldThrow<IllegalArgumentException> {
                        it.asSnowflake()
                    }.message shouldBe "id must be greater than ${MIN_SNOWFLAKE - 1}"
                }
            }
        }

        context("Safe converts should wrap error in data-type") {
            test("Non-digit string should produce NotANumber") {
                Exhaustive.azstring(1..30).generate(RandomSource.Default).map { it.value }.take(testCount).forAll {
                    val snowflake = it.asSnowflakeSafe(Either.applicativeError()).fix()
                    snowflake.shouldBeLeft { err ->
                        err shouldBe NotNumber
                    }
                }
            }

            test("Numbers, that less than $MIN_SNOWFLAKE") {
                Arb.long(max = MIN_SNOWFLAKE - 2)
                    .values(RandomSource.Default)
                    .take(testCount)
                    .map { it.value }.forAll {
                        val snowflake = it.toString()
                            .asSnowflakeSafe(Either.applicativeError()).fix()

                        snowflake.shouldBeLeft { err ->
                            err shouldBe LessThenDiscordEpoch
                        }
                    }
            }

            test("Right snowflake should unwrap without errors") {
                snowflakes.forAll {
                    it.toString()
                        .asSnowflakeSafe(Either.applicativeError())
                        .fix().shouldBeRight()
                }
            }
        }
    }
}