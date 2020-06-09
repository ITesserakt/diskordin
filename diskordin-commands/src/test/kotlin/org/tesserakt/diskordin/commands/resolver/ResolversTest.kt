package org.tesserakt.diskordin.commands.resolver

import arrow.Kind
import arrow.core.*
import arrow.core.extensions.id.applicative.applicative
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.sequence.traverse.sequence
import arrow.mtl.EitherT
import arrow.mtl.EitherTPartialOf
import arrow.mtl.extensions.eithert.applicative.applicative
import arrow.mtl.extensions.eithert.applicative.map
import arrow.typeclasses.Monad
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.beOfType
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropertyTesting
import io.kotest.property.arbitrary.arb
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.arbitrary.take
import org.tesserakt.diskordin.commands.CommandContext
import org.tesserakt.diskordin.core.data.Identified
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.entity.IMessage
import org.tesserakt.diskordin.core.entity.IMessageChannel
import org.tesserakt.diskordin.core.entity.IUser
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.log10

class ResolversTest : FunSpec() {
    private val testCount = PropertyTesting.defaultIterationCount.coerceAtMost(256)

    private fun <T : Any, F, C : CommandContext<F>> Monad<F>.test(
        resolver: TypeResolver<T, F, C>,
        badInput: Arb<String>,
        goodInput: Arb<String>,
        ctx: C
    ): Kind<F, Tuple2<Either<ParseError, SequenceK<T>>, Either<ParseError, SequenceK<T>>>> {
        val badResult = badInput
            .take(testCount).map { resolver.parse(ctx, it) }
            .sequence<EitherTPartialOf<ParseError, F>, T>(EitherT.applicative(this))
            .map(this) { it.fix() }

        val goodResult = goodInput
            .take(testCount).map { resolver.parse(ctx, it) }
            .sequence<EitherTPartialOf<ParseError, F>, T>(EitherT.applicative(this))
            .map(this) { it.fix() }

        return badResult.value().product(goodResult.value())
    }

    init {
        fun <F> fakeContext(): CommandContext<F> = object : CommandContext<F> {
            override val message: Identified<IMessage>
                get() = error("Fake")
            override val author: Identified<IUser>
                get() = error("Fake")
            override val commandArgs: Array<String>
                get() = error("Fake")
            override val channel: IdentifiedF<F, IMessageChannel>
                get() = error("Fake")
        }

        test("Boolean resolver") {
            val bad = Arb.string(0, 100)
            val good = arb { listOf("true", "TRuE", "FalSe", "false").asSequence() }
            val (fail, success) = Id.monad().test(BooleanResolver(Id.applicative()), bad, good, fakeContext()).extract()

            fail shouldBeLeft { it shouldBe beOfType<BooleanResolver.BooleanConversionError>() }
            success shouldBeRight { it.toList() shouldBe listOf(true, true, false, false) }
        }

        test("String resolver") {
            val bad = Arb.string(0..100)
            val good = Arb.string(0..100)
            val (fail, success) = Id.monad().test(StringResolver(Id.applicative()), bad, good, fakeContext()).extract()

            fail.shouldBeRight()
            success.shouldBeRight()
        }

        test("Char resolver") {
            val bad = Arb.string(2..100)
            val good = Arb.stringPattern(".")
            val (fail, success) = Id.monad().test(CharResolver(Id.applicative()), bad, good, fakeContext()).extract()

            fail.shouldBeLeft { it shouldBe beOfType<CharResolver.LengthError>() }
            success.shouldBeRight()
        }

        context("Number resolvers") {
            fun generateNumberValues(maxSize: Int) =
                Arb.stringPattern("-?\\d{1,$maxSize}")

            fun <N : Number> generateAndTest(maxValue: N, resolver: TypeResolver<N, ForId, CommandContext<ForId>>) {
                val length = log10(maxValue.toFloat()).toInt().coerceAtMost(32)
                println("Next length is $length")
                val bad = Arb.string(0, length)
                val (fail, success) = Id.monad()
                    .test(resolver, bad, generateNumberValues(length), fakeContext())
                    .extract()

                fail.shouldBeLeft()
                success.shouldBeRight()
            }

            test("Int") { generateAndTest(Int.MAX_VALUE, IntResolver(Id.applicative())) }
            test("Long") { generateAndTest(Long.MAX_VALUE, LongResolver(Id.applicative())) }
            test("Short") { generateAndTest(Short.MAX_VALUE, ShortResolver(Id.applicative())) }
            test("Byte") { generateAndTest(Byte.MAX_VALUE, ByteResolver(Id.applicative())) }
            test("Float") { generateAndTest(Float.MAX_VALUE, FloatResolver(Id.applicative())) }
            test("Double") { generateAndTest(Double.MAX_VALUE, DoubleResolver(Id.applicative())) }
            test("BigInteger") {
                generateAndTest(BigInteger.valueOf(1e20.toLong()), BigIntegerResolver(Id.applicative()))
            }
            test("BigDecimal") {
                generateAndTest(BigDecimal.valueOf(1e20), BigDecimalResolver(Id.applicative()))
            }
        }
    }
}