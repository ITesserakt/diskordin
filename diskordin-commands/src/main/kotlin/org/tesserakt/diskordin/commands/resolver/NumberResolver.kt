@file:Suppress("unused")

package org.tesserakt.diskordin.commands.resolver

import arrow.mtl.EitherT
import arrow.typeclasses.Applicative
import org.tesserakt.diskordin.commands.CommandContext
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass

sealed class NumberResolver<N : Number, F>(private val AP: Applicative<F>) : TypeResolver<N, F, CommandContext<F>> {
    data class NumberConversionError(val input: String, val to: String) : ConversionError(input, to)

    override fun parse(context: CommandContext<F>, input: String): EitherT<out ParseError, F, N> {
        val number = try {
            convert(input)
        } catch (e: Throwable) {
            null
        }

        return if (number == null)
            EitherT.left(AP, NumberConversionError(input, type.simpleName ?: "Number"))
        else
            EitherT.right(AP, number)
    }

    abstract val type: KClass<N>
    abstract fun convert(input: String): N?
}

class IntResolver<F>(AP: Applicative<F>) : NumberResolver<Int, F>(AP) {
    override val type: KClass<Int> = Int::class

    override fun convert(input: String): Int? = input.toIntOrNull()
}

class LongResolver<F>(AP: Applicative<F>) : NumberResolver<Long, F>(AP) {
    override val type: KClass<Long> = Long::class

    override fun convert(input: String): Long? = input.toLongOrNull()
}

class ShortResolver<F>(AP: Applicative<F>) : NumberResolver<Short, F>(AP) {
    override val type: KClass<Short> = Short::class

    override fun convert(input: String): Short? = input.toShortOrNull()
}

class FloatResolver<F>(AP: Applicative<F>) : NumberResolver<Float, F>(AP) {
    override val type: KClass<Float> = Float::class

    override fun convert(input: String): Float? = input.toFloatOrNull()
}

class DoubleResolver<F>(AP: Applicative<F>) : NumberResolver<Double, F>(AP) {
    override val type: KClass<Double> = Double::class

    override fun convert(input: String): Double? = input.toDoubleOrNull()
}

class BigDecimalResolver<F>(AP: Applicative<F>) : NumberResolver<BigDecimal, F>(AP) {
    override val type: KClass<BigDecimal> = BigDecimal::class

    override fun convert(input: String): BigDecimal? = input.toBigDecimalOrNull()
}

class BigIntegerResolver<F>(AP: Applicative<F>) : NumberResolver<BigInteger, F>(AP) {
    override val type: KClass<BigInteger> = BigInteger::class

    override fun convert(input: String): BigInteger? = input.toBigIntegerOrNull()
}

class ByteResolver<F>(AP: Applicative<F>) : NumberResolver<Byte, F>(AP) {
    override val type: KClass<Byte> = Byte::class

    override fun convert(input: String): Byte? = input.toByteOrNull()
}