@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package org.tesserakt.diskordin.impl.util.typeclass

import arrow.core.extensions.*
import org.tesserakt.diskordin.util.typeclass.Numeric

interface IntNumeric : Numeric<Int>, IntOrder {
    override fun Int.toInt(): Int = this
    override fun Int.toLong(): Long = this.toLong()
    override fun Int.toDouble(): Double = this.toDouble()
    override fun Int.toFloat(): Float = this.toFloat()

    override operator fun Int.plus(b: Int): Int = this + b
    override operator fun Int.minus(b: Int): Int = this - b
    override operator fun Int.times(x: Int): Int = this * x

    override fun Int.fromInt(): Int = this
    override fun Long.fromLong(): Int = this.toInt()
    override fun Float.fromFloat(): Int = this.toInt()
    override fun Double.fromDouble(): Int = this.toInt()

    override fun Int.compare(b: Int): Int = this.compareTo(b)
}

interface LongNumeric : Numeric<Long>, LongOrder {
    override fun Long.toInt(): Int = this.toInt()
    override fun Long.toLong(): Long = this
    override fun Long.toDouble(): Double = this.toDouble()
    override fun Long.toFloat(): Float = this.toFloat()

    override operator fun Long.plus(b: Long) = this + b
    override operator fun Long.minus(b: Long) = this - b
    override operator fun Long.times(x: Long) = this * x

    override fun Int.fromInt() = this.toLong()
    override fun Long.fromLong() = this
    override fun Float.fromFloat() = this.toLong()
    override fun Double.fromDouble() = this.toLong()
}

interface FloatNumeric : Numeric<Float>, FloatOrder {
    override fun Float.toInt(): Int = this.toInt()
    override fun Float.toLong(): Long = this.toLong()
    override fun Float.toDouble(): Double = this.toDouble()
    override fun Float.toFloat(): Float = this

    override operator fun Float.plus(b: Float): Float = this + b
    override operator fun Float.minus(b: Float): Float = this - b
    override operator fun Float.times(x: Float): Float = this * x

    override fun Int.fromInt() = this.toFloat()
    override fun Long.fromLong() = toFloat()
    override fun Float.fromFloat() = toFloat()
    override fun Double.fromDouble() = toFloat()
}

interface DoubleNumeric : Numeric<Double>, DoubleOrder {
    override fun Double.toInt(): Int = this.toInt()
    override fun Double.toLong(): Long = this.toLong()
    override fun Double.toDouble(): Double = this
    override fun Double.toFloat(): Float = this.toFloat()

    override operator fun Double.plus(b: Double): Double = this + b
    override operator fun Double.minus(b: Double): Double = this - b
    override operator fun Double.times(x: Double): Double = this * x

    override fun Int.fromInt() = toDouble()
    override fun Long.fromLong() = toDouble()
    override fun Float.fromFloat() = toDouble()
    override fun Double.fromDouble() = toDouble()
}

interface ShortNumeric : Numeric<Short>, ShortOrder {
    override fun Short.toInt() = toInt()
    override fun Short.toLong(): Long = toLong()
    override fun Short.toDouble(): Double = toDouble()
    override fun Short.toFloat(): Float = toFloat()

    override fun Short.plus(b: Short): Short = (this + b).toShort()
    override fun Short.minus(b: Short): Short = (this - b).toShort()
    override fun Short.times(x: Short): Short = (this * x).toShort()

    override fun Int.fromInt(): Short = toShort()
    override fun Long.fromLong(): Short = toShort()
    override fun Float.fromFloat(): Short = toShort()
    override fun Double.fromDouble(): Short = toShort()
}

fun Int.Companion.numeric() = object : IntNumeric {}
fun Long.Companion.numeric() = object : LongNumeric {}
fun Float.Companion.numeric() = object : FloatNumeric {}
fun Double.Companion.numeric() = object : DoubleNumeric {}
fun Short.Companion.numeric() = object : ShortNumeric {}