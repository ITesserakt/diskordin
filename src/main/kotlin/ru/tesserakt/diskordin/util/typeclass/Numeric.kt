package ru.tesserakt.diskordin.util.typeclass

import arrow.typeclasses.Order

interface Numeric<F> : Order<F> {
    fun F.toInt(): Int
    fun F.toLong(): Long
    fun F.toDouble(): Double
    fun F.toFloat(): Float

    infix operator fun F.plus(b: F): F
    infix operator fun F.minus(b: F): F
    infix operator fun F.times(x: F): F

    fun Int.fromInt(): F
    fun Long.fromLong(): F
    fun Float.fromFloat(): F
    fun Double.fromDouble(): F
}