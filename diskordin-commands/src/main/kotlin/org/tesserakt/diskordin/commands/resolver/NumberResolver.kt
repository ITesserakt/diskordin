package org.tesserakt.diskordin.commands.resolver

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import org.tesserakt.diskordin.commands.CommandContext
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass

sealed class NumberResolver<N : Number> : TypeResolver<N, CommandContext> {
    data class NumberConversionError(val input: String, val to: String) : ConversionError(input, to)

    override suspend fun parse(context: CommandContext, input: String) = Either.catch { convert(input) }
        .flatMap { it?.right() ?: Throwable().left() }
        .mapLeft { NumberConversionError(input, type.simpleName ?: "number") }

    abstract val type: KClass<N>
    abstract fun convert(input: String): N?
}

class IntResolver : NumberResolver<Int>() {
    override val type: KClass<Int> = Int::class

    override fun convert(input: String): Int? = input.toIntOrNull()
}

class LongResolver : NumberResolver<Long>() {
    override val type: KClass<Long> = Long::class

    override fun convert(input: String): Long? = input.toLongOrNull()
}

class ShortResolver : NumberResolver<Short>() {
    override val type: KClass<Short> = Short::class

    override fun convert(input: String): Short? = input.toShortOrNull()
}

class ByteResolver : NumberResolver<Byte>() {
    override val type: KClass<Byte> = Byte::class

    override fun convert(input: String): Byte? = input.toByteOrNull()
}

class FloatResolver : NumberResolver<Float>() {
    override val type: KClass<Float> = Float::class

    override fun convert(input: String): Float? = input.toFloatOrNull()
}

class DoubleResolver : NumberResolver<Double>() {
    override val type: KClass<Double> = Double::class

    override fun convert(input: String): Double? = input.toDoubleOrNull()
}

class BigIntegerResolver : NumberResolver<BigInteger>() {
    override val type: KClass<BigInteger> = BigInteger::class

    override fun convert(input: String): BigInteger? = input.toBigIntegerOrNull()
}

class BigDecimalResolver : NumberResolver<BigDecimal>() {
    override val type: KClass<BigDecimal> = BigDecimal::class

    override fun convert(input: String): BigDecimal? = input.toBigDecimalOrNull()
}