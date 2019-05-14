package ru.tesserakt.diskordin.util

import java.util.*

infix fun <T : Enum<T>> EnumSet<T>.or(other: T): EnumSet<T> = EnumSet.copyOf(this union listOf(other))
infix fun <T : Enum<T>> EnumSet<T>.subtract(other: T): EnumSet<T> = EnumSet.copyOf(this subtract listOf(other))
infix fun <T : Enum<T>> EnumSet<T>.and(other: T): EnumSet<T> = EnumSet.copyOf(this intersect listOf(other))

inline operator fun <reified T : Enum<T>> EnumSet<T>.not(): EnumSet<T> {
    val all = EnumSet.allOf(T::class.java)
    all.removeIf { it in this }
    return all
}

operator fun <T : Enum<T>> EnumSet<T>.plus(other: T): EnumSet<T> = this or other
operator fun <T : Enum<T>> EnumSet<T>.minus(other: T): EnumSet<T> = this subtract other
operator fun <T : Enum<T>> EnumSet<T>.times(other: T): EnumSet<T> = this and other