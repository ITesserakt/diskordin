@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package org.tesserakt.diskordin.impl.util.typeclass

import org.tesserakt.diskordin.util.typeclass.Integral
import kotlin.experimental.inv

interface IntIntegral : Integral<Int>, IntNumeric {
    override fun Int.xor(b: Int): Int = this xor b
    override fun Int.and(b: Int): Int = this and b
    override fun Int.or(b: Int): Int = this or b
    override fun Int.not(): Int = this.inv()
}

interface LongIntegral : Integral<Long>, LongNumeric {
    override fun Long.xor(b: Long): Long = this xor b
    override fun Long.and(b: Long): Long = this and b
    override fun Long.or(b: Long): Long = this or b
    override fun Long.not(): Long = inv()
}

interface ShortIntegral : Integral<Short>, ShortNumeric {
    override fun Short.xor(b: Short): Short = this.toInt().xor(b.toInt()).toShort()
    override fun Short.and(b: Short): Short = this.toInt().and(b.toInt()).toShort()
    override fun Short.or(b: Short): Short = this.toInt().or(b.toInt()).toShort()
    override fun Short.not(): Short = inv()
}

interface ByteIntegral : Integral<Byte>, ByteNumeric {
    override fun Byte.xor(b: Byte): Byte = this.toInt().xor(b.toInt()).toByte()
    override fun Byte.and(b: Byte): Byte = this.toInt().and(b.toInt()).toByte()
    override fun Byte.or(b: Byte): Byte = this.toInt().or(b.toInt()).toByte()
    override fun Byte.not(): Byte = inv()
}

private val int = object : IntIntegral {}
private val long = object : LongIntegral {}
private val short = object : ShortIntegral {}
private val byte = object : ByteIntegral {}

fun Int.Companion.integral() = int
fun Long.Companion.integral() = long
fun Short.Companion.integral() = short
fun Byte.Companion.integral() = byte
