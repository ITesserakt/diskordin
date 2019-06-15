package ru.tesserakt.diskordin.util

import java.util.*

infix fun <T : Enum<T>> EnumSet<T>.or(other: EnumSet<T>): EnumSet<T> = EnumSet.copyOf(this union other)
infix fun <T : Enum<T>> EnumSet<T>.xor(other: EnumSet<T>): EnumSet<T> = EnumSet.copyOf(this subtract other)
infix fun <T : Enum<T>> EnumSet<T>.and(other: EnumSet<T>): EnumSet<T> = EnumSet.copyOf(this intersect other)

inline operator fun <reified T : Enum<T>> EnumSet<T>.not(): EnumSet<T> {
    val all = EnumSet.allOf(T::class.java)
    all.removeIf { it in this }
    return all
}

operator fun <T : Enum<T>> EnumSet<T>.plus(other: EnumSet<T>): EnumSet<T> = this or other
operator fun <T : Enum<T>> EnumSet<T>.minus(other: EnumSet<T>): EnumSet<T> = this xor other
operator fun <T : Enum<T>> EnumSet<T>.times(other: EnumSet<T>): EnumSet<T> = this and other