package org.tesserakt.diskordin.util.typeclass

import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive
import org.tesserakt.diskordin.impl.util.typeclass.BigDecimalK
import org.tesserakt.diskordin.impl.util.typeclass.numeric

class NumericTest : StringSpec({
    include("Int ", Int.numeric().test())
    include("Long ", Long.numeric().test())
    include("Double ", Double.numeric().test())
    include("Float ", Float.numeric().test())
    include("Byte ", Byte.numeric().test())
    include("Short ", Short.numeric().test())
    include("BigDecimal ", BigDecimalK.numeric().test())
})

private fun <N : Number> Numeric<N>.test() = stringSpec {
    "Converting" {
        zero.toInt() shouldBe 0
        zero.toFloat() shouldBe 0f
        zero.toLong() shouldBe 0L
        zero.toDouble() shouldBe 0.0
    }
    "Addition" {
        forAll(
            row(1.fromInt(), 2.fromInt(), 3.fromInt()),
            row((-1).fromInt(), (-7).fromInt(), (-8).fromInt()),
            row(zero, zero, zero),
            row((-2).fromInt(), 5.fromInt(), 3.fromInt())
        ) { a, b, sum ->
            a + b shouldBe sum
        }
    }
    "Subtraction" {
        forAll(
            row(1.fromInt(), 2.fromInt(), (-1).fromInt()),
            row((-1).fromInt(), (-7).fromInt(), 6.fromInt()),
            row(zero, zero, zero),
            row((-2).fromInt(), 5.fromInt(), (-7).fromInt())
        ) { a, b, diff ->
            a - b shouldBe diff
        }
    }
    "Multiplication" {
        forAll(
            row(1.fromInt(), 2.fromInt(), 2.fromInt()),
            row((-1).fromInt(), (-7).fromInt(), 7.fromInt()),
            row(zero, zero, zero),
            row((-2).fromInt(), 5.fromInt(), (-10).fromInt())
        ) { a, b, product ->
            a * b shouldBe product
        }
    }
    "Absolute" {
        val numbers = Arb.bind(Arb.long(), Arb.int(), Arb.double(), Arb.float()) { long, int, double, float ->
            listOf(long.fromLong(), double.fromDouble(), int.fromInt(), float.fromFloat()).exhaustive()
        }.flatMap { it.toArb() }

        checkAll(numbers) { a: N ->
            (a.abs() >= zero) shouldBe true
        }
    }
}