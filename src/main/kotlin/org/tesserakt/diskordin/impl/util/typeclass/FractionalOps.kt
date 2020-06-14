@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package org.tesserakt.diskordin.impl.util.typeclass

import org.tesserakt.diskordin.util.typeclass.Fractional
import java.math.BigDecimal

interface FloatFractional : Fractional<Float>, FloatNumeric {
    override fun Float.div(b: Float): Float = this / b
}

interface DoubleFractional : Fractional<Double>, DoubleNumeric {
    override fun Double.div(b: Double): Double = this / b
}

interface BigDecimalFractional : Fractional<BigDecimal>, BigDecimalNumeric {
    override fun BigDecimal.div(b: BigDecimal): BigDecimal = this.divide(b)
}

private val float = object : FloatFractional {}
private val double = object : DoubleFractional {}
private val bigDecimal = object : BigDecimalFractional {}

fun Float.Companion.fractional() = float
fun Double.Companion.fractional() = double
fun BigDecimalK.Companion.fractional() = bigDecimal