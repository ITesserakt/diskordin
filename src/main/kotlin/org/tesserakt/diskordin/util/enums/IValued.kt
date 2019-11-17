@file:Suppress("unused")

package org.tesserakt.diskordin.util.enums

import org.tesserakt.diskordin.util.typeclass.Integral
import java.util.*

interface IValued<E : Enum<E>, N> : Integral<N> {
    val value: N
}

infix fun <E, N> IValued<E, N>.and(other: IValued<E, N>)
        where E : Enum<E>, E : IValued<E, N> =
    ValuedEnum<E, N>(value and other.value, this)

infix fun <E, N> IValued<E, N>.or(other: IValued<E, N>)
        where E : Enum<E>, E : IValued<E, N> =
    ValuedEnum<E, N>(value or other.value, this)

infix fun <E, N> IValued<E, N>.xor(other: IValued<E, N>)
        where E : Enum<E>, E : IValued<E, N> =
    ValuedEnum<E, N>(value xor other.value, this)

infix fun <E, N> IValued<E, N>.and(other: ValuedEnum<E, N>)
        where E : Enum<E>, E : IValued<E, N> =
    ValuedEnum<E, N>(value and other.code, this)

infix fun <E, N> IValued<E, N>.or(other: ValuedEnum<E, N>)
        where E : Enum<E>, E : IValued<E, N> =
    ValuedEnum<E, N>(value or other.code, this)

infix fun <E, N> IValued<E, N>.xor(other: ValuedEnum<E, N>)
        where E : Enum<E>, E : IValued<E, N> =
    ValuedEnum<E, N>(value xor other.code, this)

operator fun <E, N> E.not()
        where E : Enum<E>, E : IValued<E, N> = ValuedEnum<E, N>(
    this@not.declaringClass.enumConstants
        .map(IValued<E, N>::value)
        .reduce { acc, i -> acc + i } - value, this
)

inline operator fun <reified E, N> ValuedEnum<E, N>.not()
        where E : Enum<E>, E : IValued<E, N> = ValuedEnum<E, N>(
    E::class.java.enumConstants
        .map { it.value }
        .reduce { acc, n -> acc + n } - code, integral
)

inline fun <reified E, N> ValuedEnum<E, N>.asSet(): EnumSet<E>
        where E : Enum<E>, E : IValued<E, N> = EnumSet.allOf(E::class.java).apply {
    removeIf {
        code and it.value != it.value
    }
}

fun <E, N> EnumSet<E>.enhance(integral: Integral<N>)
        where E : Enum<E>, E : IValued<E, N> = with(integral) {
    ValuedEnum<E, N>(map { it.value }.reduce { acc, n -> acc + n }, integral)
}
