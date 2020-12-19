package org.tesserakt.diskordin.util.typeclass

import arrow.typeclasses.Order

interface Numeric<F> : Order<F> {
    fun F.toInt(): Int
    fun F.toLong(): Long
    fun F.toDouble(): Double
    fun F.toFloat(): Float

    infix operator fun F.plus(b: F): F
    infix operator fun F.times(x: F): F
    operator fun F.unaryMinus(): F

    infix operator fun F.minus(b: F): F = this + -b
    fun F.abs() = when {
        this == minValue -> maxValue
        this < zero -> -this
        else -> this
    }

    fun F.sgn() = when {
        this > zero -> 1
        this < zero -> -1
        else -> 0
    }

    fun Int.fromInt(): F
    fun Long.fromLong(): F
    fun Float.fromFloat(): F
    fun Double.fromDouble(): F

    val maxValue: F
    val minValue: F
    val zero get() = 0.fromInt()
}