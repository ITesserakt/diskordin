package org.tesserakt.diskordin.commands.resolver

import arrow.core.*
import arrow.core.extensions.either.applicative.applicative
import arrow.core.extensions.either.applicative.map
import arrow.core.extensions.list.traverse.sequence
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beOfType
import io.kotest.property.Arb
import io.kotest.property.PropertyTesting
import io.kotest.property.arbitrary.arb
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.arbitrary.take
import org.tesserakt.diskordin.commands.CommandContext
import org.tesserakt.diskordin.core.data.Identified
import org.tesserakt.diskordin.core.data.IdentifiedIO
import org.tesserakt.diskordin.core.entity.IMessage
import org.tesserakt.diskordin.core.entity.IMessageChannel
import org.tesserakt.diskordin.core.entity.IUser
import java.math.BigDecimal
import java.math.BigInteger

class ResolversTest : FunSpec() {
    private val testCount = PropertyTesting.defaultIterationCount.coerceAtMost(256)

    private suspend fun <T : Any, C : CommandContext> test(
        resolver: TypeResolver<T, C>,
        badInput: Arb<String>,
        goodInput: Arb<String>,
        ctx: C
    ): Tuple2<Either<ParseError, ListK<T>>, Either<ParseError, ListK<T>>> {
        val badResult = badInput
            .take(testCount).toList().map { resolver.parse(ctx, it) }
            .sequence(Either.applicative()).map { it.fix() }

        val goodResult = goodInput
            .take(testCount).toList().map { resolver.parse(ctx, it) }
            .sequence(Either.applicative()).map { it.fix() }

        return badResult toT goodResult
    }

    init {
        val fakeContext = object : CommandContext {
            override val message: Identified<IMessage>
                get() = error("Fake")
            override val author: Identified<IUser>
                get() = error("Fake")
            override val commandArgs: Array<String>
                get() = error("Fake")
            override val channel: IdentifiedIO<IMessageChannel>
                get() = error("Fake")
        }

        test("Boolean resolver") {
            val bad = Arb.string(0, 100)
            val good = arb { listOf("true", "TRuE", "FalSe", "false").asSequence() }
            val (fail, success) = test(BooleanResolver(), bad, good, fakeContext)

            fail shouldBeLeft { it shouldBe beOfType<BooleanResolver.BooleanConversionError>() }
            success shouldBeRight { it.toList() shouldBe listOf(true, true, false, false) }
        }

        test("String resolver") {
            val bad = Arb.string(0..100)
            val good = Arb.string(0..100)
            val (fail, success) = test(StringResolver(), bad, good, fakeContext)

            fail.shouldBeRight()
            success.shouldBeRight()
        }

        test("Char resolver") {
            val bad = Arb.string(2..100)
            val good = Arb.stringPattern(".")
            val (fail, success) = test(CharResolver(), bad, good, fakeContext)

            fail.shouldBeLeft { it shouldBe beOfType<CharResolver.LengthError>() }
            success.shouldBeRight()
        }

        context("Number resolvers") {
            fun generateNumberValues(maxSize: Int) =
                Arb.stringPattern("-?\\d{1,$maxSize}")

            suspend fun <N : Number> generateAndTest(maxValue: N, resolver: TypeResolver<N, CommandContext>) {
                val length = BigDecimal(maxValue.toString()).toPlainString().length - 1
                println("Next length is $length")
                val bad = Arb.string(0, length)
                val (fail, success) = test(resolver, bad, generateNumberValues(length), fakeContext)

                fail.shouldBeLeft()
                success.shouldBeRight()
            }

            test("Int") { generateAndTest(Int.MAX_VALUE, IntResolver()) }
            test("Long") { generateAndTest(Long.MAX_VALUE, LongResolver()) }
            test("Short") { generateAndTest(Short.MAX_VALUE, ShortResolver()) }
            test("Byte") { generateAndTest(Byte.MAX_VALUE, ByteResolver()) }
            test("Float") { generateAndTest(Float.MAX_VALUE, FloatResolver()) }
            test("Double") { generateAndTest(Double.MAX_VALUE, DoubleResolver()) }
            test("BigInteger") { generateAndTest(BigInteger.valueOf(10).pow(1024), BigIntegerResolver()) }
            test("BigDecimal") { generateAndTest(BigDecimal(10).pow(1024), BigDecimalResolver()) }
        }
    }
}