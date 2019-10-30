@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package ru.tesserakt.diskordin.util.typeclass

import kotlin.experimental.inv

interface IntIntegral : Integral<Int>, IntNumeric {
    override fun Int.xor(b: Int): Int = this xor b
    override fun Int.and(b: Int): Int = this and b
    override fun Int.or(b: Int): Int = this or b
    override fun Int.not(): Int = this.inv()
}

fun Int.Companion.integral() = object : IntIntegral {}

interface LongIntegral : Integral<Long>, LongNumeric {
    override fun Long.xor(b: Long): Long = this xor b
    override fun Long.and(b: Long): Long = this and b
    override fun Long.or(b: Long): Long = this or b
    override fun Long.not(): Long = inv()
}

fun Long.Companion.integral() = object : LongIntegral {}

interface ShortIntegral : Integral<Short>, ShortNumeric {
    override fun Short.xor(b: Short): Short = this.toInt().xor(b.toInt()).toShort()
    override fun Short.and(b: Short): Short = this.toInt().and(b.toInt()).toShort()
    override fun Short.or(b: Short): Short = this.toInt().or(b.toInt()).toShort()
    override fun Short.not(): Short = inv()
}

fun Short.Companion.integral() = object : ShortIntegral {}
