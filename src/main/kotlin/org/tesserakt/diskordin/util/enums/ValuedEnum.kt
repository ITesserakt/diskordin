package org.tesserakt.diskordin.util.enums

import org.tesserakt.diskordin.util.typeclass.Integral

class ValuedEnum<E, I>(val code: I, val integral: Integral<I>) : Integral<I> by integral
        where E : Enum<E>, E : IValued<E, I> {
    infix fun and(other: IValued<E, I>) =
        ValuedEnum<E, I>(code and other.value, integral)

    infix fun or(other: IValued<E, I>) =
        ValuedEnum<E, I>(code or other.value, integral)

    infix fun xor(other: IValued<E, I>) =
        ValuedEnum<E, I>(code xor other.value, integral)

    infix fun and(other: ValuedEnum<E, I>) =
        ValuedEnum<E, I>(code and other.code, integral)

    infix fun or(other: ValuedEnum<E, I>) =
        ValuedEnum<E, I>(code or other.code, integral)

    infix fun xor(other: ValuedEnum<E, I>) =
        ValuedEnum<E, I>(code xor other.code, integral)

    operator fun contains(other: IValued<E, I>) = code and other.value == other.value
    operator fun contains(other: ValuedEnum<E, I>) = code and other.code == other.code
}