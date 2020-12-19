@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package org.tesserakt.diskordin.impl.util.typeclass

import arrow.core.Ordering
import arrow.core.extensions.*
import org.tesserakt.diskordin.util.typeclass.Numeric
import java.math.BigDecimal

interface IntNumeric : Numeric<Int>, IntOrder {
    override fun Int.toInt(): Int = this
    override fun Int.toLong(): Long = this.toLong()
    override fun Int.toDouble(): Double = this.toDouble()
    override fun Int.toFloat(): Float = this.toFloat()

    override operator fun Int.plus(b: Int): Int = this + b
    override operator fun Int.times(x: Int): Int = this * x
    override fun Int.unaryMinus(): Int = 0 - this

    override fun Int.fromInt(): Int = this
    override fun Long.fromLong(): Int = this.toInt()
    override fun Float.fromFloat(): Int = this.toInt()
    override fun Double.fromDouble(): Int = this.toInt()

    override val maxValue get() = Int.MAX_VALUE
    override val minValue get() = Int.MIN_VALUE
}

interface LongNumeric : Numeric<Long>, LongOrder {
    override fun Long.toInt(): Int = this.toInt()
    override fun Long.toLong(): Long = this
    override fun Long.toDouble(): Double = this.toDouble()
    override fun Long.toFloat(): Float = this.toFloat()

    override operator fun Long.plus(b: Long) = this + b
    override operator fun Long.times(x: Long) = this * x
    override fun Long.unaryMinus(): Long = 0 - this

    override fun Int.fromInt() = this.toLong()
    override fun Long.fromLong() = this
    override fun Float.fromFloat() = this.toLong()
    override fun Double.fromDouble() = this.toLong()

    override val maxValue get() = Long.MAX_VALUE
    override val minValue get() = Long.MIN_VALUE
}

interface FloatNumeric : Numeric<Float>, FloatOrder {
    override fun Float.toInt(): Int = this.toInt()
    override fun Float.toLong(): Long = this.toLong()
    override fun Float.toDouble(): Double = this.toDouble()
    override fun Float.toFloat(): Float = this

    override operator fun Float.plus(b: Float): Float = this + b
    override operator fun Float.times(x: Float): Float = this * x
    override fun Float.unaryMinus(): Float = -this

    override fun Int.fromInt() = this.toFloat()
    override fun Long.fromLong() = toFloat()
    override fun Float.fromFloat() = toFloat()
    override fun Double.fromDouble() = toFloat()

    override val maxValue get() = Float.MAX_VALUE
    override val minValue get() = Float.MIN_VALUE
}

interface DoubleNumeric : Numeric<Double>, DoubleOrder {
    override fun Double.toInt(): Int = this.toInt()
    override fun Double.toLong(): Long = this.toLong()
    override fun Double.toDouble(): Double = this
    override fun Double.toFloat(): Float = this.toFloat()

    override operator fun Double.plus(b: Double): Double = this + b
    override operator fun Double.times(x: Double): Double = this * x
    override fun Double.unaryMinus(): Double = -this

    override fun Int.fromInt() = toDouble()
    override fun Long.fromLong() = toDouble()
    override fun Float.fromFloat() = toDouble()
    override fun Double.fromDouble() = toDouble()

    override val maxValue get() = Double.MAX_VALUE
    override val minValue get() = Double.MIN_VALUE
}

interface ShortNumeric : Numeric<Short>, ShortOrder {
    override fun Short.toInt() = toInt()
    override fun Short.toLong(): Long = toLong()
    override fun Short.toDouble(): Double = toDouble()
    override fun Short.toFloat(): Float = toFloat()

    override fun Short.plus(b: Short): Short = (this + b).toShort()
    override fun Short.times(x: Short): Short = (this * x).toShort()
    override fun Short.unaryMinus(): Short = (0 - this).toShort()

    override fun Int.fromInt(): Short = toShort()
    override fun Long.fromLong(): Short = toShort()
    override fun Float.fromFloat(): Short = toInt().toShort()
    override fun Double.fromDouble(): Short = toInt().toShort()

    override val maxValue get() = Short.MAX_VALUE
    override val minValue get() = Short.MIN_VALUE
}

interface ByteNumeric : Numeric<Byte>, ByteOrder {
    override fun Byte.toInt(): Int = toInt()
    override fun Byte.toLong(): Long = toLong()
    override fun Byte.toDouble(): Double = toDouble()
    override fun Byte.toFloat(): Float = toFloat()

    override fun Byte.plus(b: Byte): Byte = this.plus(b).toByte()
    override fun Byte.times(x: Byte): Byte = this.times(x).toByte()
    override fun Byte.unaryMinus(): Byte = (0 - this).toByte()

    override fun Int.fromInt(): Byte = toByte()
    override fun Long.fromLong(): Byte = toByte()
    override fun Float.fromFloat(): Byte = toInt().toByte()
    override fun Double.fromDouble(): Byte = toInt().toByte()

    override val maxValue get() = Byte.MAX_VALUE
    override val minValue get() = Byte.MIN_VALUE
}

interface BigDecimalNumeric : Numeric<BigDecimal> {
    override fun BigDecimal.toInt(): Int = toInt()
    override fun BigDecimal.toLong(): Long = toLong()
    override fun BigDecimal.toDouble(): Double = toDouble()
    override fun BigDecimal.toFloat(): Float = toFloat()

    override fun BigDecimal.plus(b: BigDecimal): BigDecimal = this.subtract(-b)
    override fun BigDecimal.times(x: BigDecimal): BigDecimal = this.multiply(x)
    override fun BigDecimal.unaryMinus(): BigDecimal = zero.subtract(this)

    override fun Int.fromInt(): BigDecimal = toBigDecimal()
    override fun Long.fromLong(): BigDecimal = toBigDecimal()
    override fun Float.fromFloat(): BigDecimal = when {
        this == Float.POSITIVE_INFINITY -> maxValue
        this == Float.NEGATIVE_INFINITY -> minValue
        this.isNaN() -> 0.toBigDecimal()
        else -> toBigDecimal()
    }

    override fun Double.fromDouble(): BigDecimal = when {
        this == Double.POSITIVE_INFINITY -> maxValue
        this == Double.NEGATIVE_INFINITY -> minValue
        this.isNaN() -> 0.toBigDecimal()
        else -> toBigDecimal()
    }

    override fun BigDecimal.compare(b: BigDecimal): Ordering = Ordering.fromInt(this.compareTo(b))

    override val maxValue: BigDecimal get() = BigDecimal(10).pow(1024) //does not maximum, but enough
    override val minValue get() = -maxValue
}

private val int = object : IntNumeric {}
private val long = object : LongNumeric {}
private val float = object : FloatNumeric {}
private val double = object : DoubleNumeric {}
private val short = object : ShortNumeric {}
private val byte = object : ByteNumeric {}
private val bigDecimal = object : BigDecimalNumeric {}

fun Int.Companion.numeric() = int
fun Long.Companion.numeric() = long
fun Float.Companion.numeric() = float
fun Double.Companion.numeric() = double
fun Short.Companion.numeric() = short
fun Byte.Companion.numeric() = byte
fun BigDecimalK.Companion.numeric() = bigDecimal

class BigDecimalK {
    companion object
}