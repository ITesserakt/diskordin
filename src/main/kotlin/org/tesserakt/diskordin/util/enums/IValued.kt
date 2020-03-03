@file:Suppress("unused")

package org.tesserakt.diskordin.util.enums

import org.tesserakt.diskordin.util.typeclass.Integral

interface IValued<E : Enum<E>, N : Any> : Integral<N> {
    val value: N
}

infix fun <E, N : Any> IValued<E, N>.and(other: IValued<E, N>)
        where E : Enum<E>, E : IValued<E, N> =
    ValuedEnum<E, N>(value and other.value, this)

infix fun <E, N : Any> IValued<E, N>.or(other: IValued<E, N>)
        where E : Enum<E>, E : IValued<E, N> =
    ValuedEnum<E, N>(value or other.value, this)

infix fun <E, N : Any> IValued<E, N>.xor(other: IValued<E, N>)
        where E : Enum<E>, E : IValued<E, N> =
    ValuedEnum<E, N>(value xor other.value, this)

infix fun <E, N : Any> IValued<E, N>.and(other: ValuedEnum<E, N>)
        where E : Enum<E>, E : IValued<E, N> =
    ValuedEnum<E, N>(value and other.code, this)

infix fun <E, N : Any> IValued<E, N>.or(other: ValuedEnum<E, N>)
        where E : Enum<E>, E : IValued<E, N> =
    ValuedEnum<E, N>(value or other.code, this)

infix fun <E, N : Any> IValued<E, N>.xor(other: ValuedEnum<E, N>)
        where E : Enum<E>, E : IValued<E, N> =
    ValuedEnum<E, N>(value xor other.code, this)

operator fun <E, N : Any> E.not()
        where E : Enum<E>, E : IValued<E, N> = ValuedEnum<E, N>(
    this@not.declaringClass.enumConstants
        .map(IValued<E, N>::value)
        .fold(0.fromInt()) { acc, i -> acc + i } - value, this
)