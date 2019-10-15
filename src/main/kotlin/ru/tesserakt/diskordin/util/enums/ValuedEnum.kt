package ru.tesserakt.diskordin.util.enums

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