@file:Suppress("unused")

package org.tesserakt.diskordin.util.enums

import org.tesserakt.diskordin.util.typeclass.Integral

interface IValued<E : Enum<E>, N : Any> : Integral<N> {
    val code: N
}

infix fun <E, N : Any> IValued<E, N>.and(other: IValued<E, N>)
        where E : Enum<E>, E : IValued<E, N> =
    ValuedEnum<E, N>(code and other.code, this)

infix fun <E, N : Any> IValued<E, N>.or(other: IValued<E, N>)
        where E : Enum<E>, E : IValued<E, N> =
    ValuedEnum<E, N>(code or other.code, this)

infix fun <E, N : Any> IValued<E, N>.xor(other: IValued<E, N>)
        where E : Enum<E>, E : IValued<E, N> =
    ValuedEnum<E, N>(code xor other.code, this)

operator fun <E, N : Any> E.not()
        where E : Enum<E>, E : IValued<E, N> = ValuedEnum<E, N>(
    this@not.declaringClass.enumConstants
        .map(IValued<E, N>::code)
        .reduce { acc, i -> acc + i } - code, this
)