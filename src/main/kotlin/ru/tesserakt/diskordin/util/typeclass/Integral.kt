package ru.tesserakt.diskordin.util.typeclass

interface Integral<F> : Numeric<F> {
    infix fun F.xor(b: F): F
    infix fun F.and(b: F): F
    infix fun F.or(b: F): F
    operator fun F.not(): F
}