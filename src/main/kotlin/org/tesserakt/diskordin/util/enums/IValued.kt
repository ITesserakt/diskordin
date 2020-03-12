@file:Suppress("unused")

package org.tesserakt.diskordin.util.enums

import org.tesserakt.diskordin.util.typeclass.Integral
import java.util.*

interface IValued<E : Enum<E>, N> : Integral<N> {
    val code: N
}

infix fun <E, N> IValued<E, N>.and(other: IValued<E, N>)
        where E : Enum<E>, E : IValued<E, N> =
    ValuedEnum<E, N>(code and other.code, this)

infix fun <E, N> IValued<E, N>.or(other: IValued<E, N>)
        where E : Enum<E>, E : IValued<E, N> =
    ValuedEnum<E, N>(code or other.code, this)

infix fun <E, N> IValued<E, N>.xor(other: IValued<E, N>)
        where E : Enum<E>, E : IValued<E, N> =
    ValuedEnum<E, N>(code xor other.code, this)

operator fun <E, N> E.not()
        where E : Enum<E>, E : IValued<E, N> = ValuedEnum<E, N>(
    this@not.declaringClass.enumConstants
        .map(IValued<E, N>::code)
        .reduce { acc, i -> acc + i } - code, this
)

inline operator fun <reified E, N> ValuedEnum<E, N>.not()
        where E : Enum<E>, E : IValued<E, N> = ValuedEnum<E, N>(
    E::class.java.enumConstants
        .map { it.code }
        .reduce { acc, n -> acc + n } - this.code, integral
)

inline fun <reified E, N> IValued<E, N>.asSet(): EnumSet<E>
        where E : Enum<E>, E : IValued<E, N> = EnumSet.allOf(E::class.java).apply {
    removeIf {
        this@asSet.code and it.code != it.code
    }
}

fun <E, N> EnumSet<E>.enhance(integral: Integral<N>)
        where E : Enum<E>, E : IValued<E, N> = with(integral) {
    ValuedEnum<E, N>(map { it.code }.reduce { acc, n -> acc + n }, integral)
}
