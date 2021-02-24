package org.tesserakt.diskordin.core.data

import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.long
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.azstring
import org.tesserakt.diskordin.core.data.Snowflake.ConstructionError.LessThenDiscordEpoch
import org.tesserakt.diskordin.core.data.Snowflake.ConstructionError.NotNumber
import kotlin.random.nextLong

private const val MIN_SNOWFLAKE = 4194305L

@ExperimentalUnsignedTypes
class SnowflakeTest : FunSpec() {
    private val snowflakes = arbitrary { it.random.nextLong(MIN_SNOWFLAKE..Long.MAX_VALUE) }

    init {
        test("Snowflake.toString should return number in string") {
            checkAll(snowflakes) {
                it.asSnowflake().toString() shouldBe "$it"
            }
        }

        context("Non-safe converts should throw errors comparing their rules") {
            test("String, that hasn't digits") {
                checkAll(Exhaustive.azstring(1..30)) {
                    shouldThrow<IllegalArgumentException> {
                        it.asSnowflake()
                    }.message shouldEndWith "cannot be represented as Snowflake"
                }
            }

            test("Number, that less then 0") {
                checkAll(Arb.long(max = 0).filter { it < 0 }) {
                    shouldThrow<IllegalArgumentException> {
                        it.asSnowflake()
                    }.message shouldBe "id must be greater than 0"
                }
            }

            test("Number, that less then $MIN_SNOWFLAKE") {
                checkAll(arbitrary { it.random.nextLong(MIN_SNOWFLAKE) }) {
                    shouldThrow<IllegalArgumentException> {
                        it.asSnowflake()
                    }.message shouldBe "id must be greater than ${MIN_SNOWFLAKE - 1}"
                }
            }
        }

        context("Safe converts should wrap error in data-type") {
            test("Non-digit string should produce NotANumber") {
                checkAll(Exhaustive.azstring(1..30)) {
                    val snowflake = it.asSnowflakeEither()
                    snowflake shouldBeLeft { err -> err shouldBe NotNumber }
                }
            }

            test("Numbers, that less than $MIN_SNOWFLAKE") {
                checkAll(arbitrary { it.random.nextLong(MIN_SNOWFLAKE) }) {
                    val snowflake = it.toString().asSnowflakeEither()

                    snowflake.shouldBeLeft { err -> err shouldBe LessThenDiscordEpoch }
                }
            }

            test("Right snowflake should unwrap without errors") {
                checkAll(snowflakes) { it.toString().asSnowflakeEither().shouldBeRight() }
            }
        }
    }
}