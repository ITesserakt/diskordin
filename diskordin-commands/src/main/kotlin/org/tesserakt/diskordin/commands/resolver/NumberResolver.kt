@file:Suppress("unused")

package org.tesserakt.diskordin.commands.resolver

import arrow.mtl.EitherT
import arrow.mtl.extensions.fx
import arrow.typeclasses.MonadThrow
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass

sealed class NumberResolver<N : Number, F>(private val MT: MonadThrow<F>) : TypeResolver<N, F, Nothing> {
    data class NumberConversionError(val input: String, val to: String) : ConversionError(input, to)

    override fun parse(context: Nothing, input: String): EitherT<out ParseError, F, N> = EitherT.fx(MT) {
        val number = !effectCatch { convert(input) }

        if (number == null)
            !EitherT.left<NumberConversionError, F, N>(MT, NumberConversionError(input, type.simpleName ?: "Number"))
        else
            !EitherT.right<ParseError, F, N>(MT, number)
    }

    abstract val type: KClass<N>
    abstract fun convert(input: String): N?
}

class IntResolver<F>(MT: MonadThrow<F>) : NumberResolver<Int, F>(MT) {
    override val type: KClass<Int> = Int::class

    override fun convert(input: String): Int? = input.toIntOrNull()
}

class LongResolver<F>(MT: MonadThrow<F>) : NumberResolver<Long, F>(MT) {
    override val type: KClass<Long> = Long::class

    override fun convert(input: String): Long? = input.toLongOrNull()
}

class ShortResolver<F>(MT: MonadThrow<F>) : NumberResolver<Short, F>(MT) {
    override val type: KClass<Short> = Short::class

    override fun convert(input: String): Short? = input.toShortOrNull()
}

class FloatResolver<F>(MT: MonadThrow<F>) : NumberResolver<Float, F>(MT) {
    override val type: KClass<Float> = Float::class

    override fun convert(input: String): Float? = input.toFloatOrNull()
}

class DoubleResolver<F>(MT: MonadThrow<F>) : NumberResolver<Double, F>(MT) {
    override val type: KClass<Double> = Double::class

    override fun convert(input: String): Double? = input.toDoubleOrNull()
}

class BigDecimalResolver<F>(MT: MonadThrow<F>) : NumberResolver<BigDecimal, F>(MT) {
    override val type: KClass<BigDecimal> = BigDecimal::class

    override fun convert(input: String): BigDecimal? = input.toBigDecimalOrNull()
}

class BigIntegerResolver<F>(MT: MonadThrow<F>) : NumberResolver<BigInteger, F>(MT) {
    override val type: KClass<BigInteger> = BigInteger::class

    override fun convert(input: String): BigInteger? = input.toBigIntegerOrNull()
}

class ByteResolver<F>(MT: MonadThrow<F>) : NumberResolver<Byte, F>(MT) {
    override val type: KClass<Byte> = Byte::class

    override fun convert(input: String): Byte? = input.toByteOrNull()
}