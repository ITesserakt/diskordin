@file:Suppress("unused")

package ru.tesserakt.diskordin.util.enums

import java.util.*

interface IValued<E : Enum<E>> {
    val value: Long
}

data class ValuedEnum<E>(val code: Long)
        where E : Enum<E>, E : IValued<E> {
    infix fun and(other: IValued<E>) =
        ValuedEnum<E>(code and other.value)

    infix fun or(other: IValued<E>) =
        ValuedEnum<E>(code or other.value)

    infix fun xor(other: IValued<E>) =
        ValuedEnum<E>(code xor other.value)

    infix fun and(other: ValuedEnum<E>) =
        ValuedEnum<E>(code and other.code)

    infix fun or(other: ValuedEnum<E>) =
        ValuedEnum<E>(code or other.code)

    infix fun xor(other: ValuedEnum<E>) =
        ValuedEnum<E>(code xor other.code)

    operator fun contains(other: IValued<E>) = code and other.value == other.value
    operator fun contains(other: ValuedEnum<E>) = code and other.code == other.code
}

infix fun <E> IValued<E>.and(other: IValued<E>)
        where E : Enum<E>, E : IValued<E> =
    ValuedEnum<E>(value and other.value)

infix fun <E> IValued<E>.or(other: IValued<E>)
        where E : Enum<E>, E : IValued<E> =
    ValuedEnum<E>(value or other.value)

infix fun <E> IValued<E>.xor(other: IValued<E>)
        where E : Enum<E>, E : IValued<E> =
    ValuedEnum<E>(value xor other.value)

infix fun <E> IValued<E>.and(other: ValuedEnum<E>)
        where E : Enum<E>, E : IValued<E> =
    ValuedEnum<E>(value and other.code)

infix fun <E> IValued<E>.or(other: ValuedEnum<E>)
        where E : Enum<E>, E : IValued<E> =
    ValuedEnum<E>(value or other.code)

infix fun <E> IValued<E>.xor(other: ValuedEnum<E>)
        where E : Enum<E>, E : IValued<E> =
    ValuedEnum<E>(value xor other.code)

operator fun <E> Enum<E>.not()
        where E : Enum<E>, E : IValued<E> = ValuedEnum<E>(
    this.declaringClass.enumConstants
        .map(IValued<E>::value)
        .reduce(Long::plus) - (this as IValued<*>).value
)

inline operator fun <reified E> ValuedEnum<E>.not()
        where E : Enum<E>, E : IValued<E> = ValuedEnum<E>(
    E::class.java.enumConstants
        .map { it.value }
        .reduce(Long::plus) - code
)

inline fun <reified E> ValuedEnum<E>.asSet(): EnumSet<E>
        where E : Enum<E>, E : IValued<E> = EnumSet.allOf(E::class.java).apply {
    removeIf {
        code and it.value != it.value
    }
}

fun <E> EnumSet<E>.enhance()
        where E : Enum<E>, E : IValued<E> =
    ValuedEnum<E>(map { it.value }.reduce(Long::plus))
