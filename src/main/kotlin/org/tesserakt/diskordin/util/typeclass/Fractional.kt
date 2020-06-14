package org.tesserakt.diskordin.util.typeclass

interface Fractional<F> : Numeric<F> {
    operator fun F.div(b: F): F
    fun F.reversed() = 1.fromInt() / this
}