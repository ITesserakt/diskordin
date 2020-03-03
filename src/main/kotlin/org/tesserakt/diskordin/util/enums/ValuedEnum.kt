package org.tesserakt.diskordin.util.enums

import org.tesserakt.diskordin.util.typeclass.Integral
import java.util.*

class ValuedEnum<E, I>(override val code: I, val integral: Integral<I>) : IValued<E, I>, Integral<I> by integral
        where E : Enum<E>, E : IValued<E, I>, I : Any {

    operator fun contains(other: IValued<E, I>) = this.code and other.code == other.code

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ValuedEnum<*, *>

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }

    override fun toString(): String {
        return "ValuedEnum(code=$code)"
    }

    companion object {
        fun <E, N> none(integral: Integral<N>)
                where E : Enum<E>, N : Any, E : IValued<E, N> = integral.run {
            ValuedEnum<E, N>(0.fromInt(), integral)
        }

        inline fun <reified E, N> all(integral: Integral<N>)
                where E : Enum<E>, N : Any, E : IValued<E, N> = integral.run {
            ValuedEnum<E, N>(
                E::class.java.enumConstants
                    .map { it.code }
                    .fold(0.fromInt()) { acc, n -> acc + n }, integral
            )
        }
    }
}

inline operator fun <reified E, N : Any> ValuedEnum<E, N>.not() where E : Enum<E>, E : IValued<E, N> =
    ValuedEnum<E, N>(ValuedEnum.all<E, N>(integral).code - code, integral)

inline fun <reified E, N : Any> ValuedEnum<E, N>.asSet(): EnumSet<E>
        where E : Enum<E>, E : IValued<E, N> = EnumSet.allOf(E::class.java).apply {
    removeIf {
        code and it.code != it.code
    }
}

fun <E, N : Any> EnumSet<E>.enhance(integral: Integral<N>)
        where E : Enum<E>, E : IValued<E, N> = with(integral) {
    ValuedEnum<E, N>(map { it.code }.fold(0.fromInt()) { acc, n -> acc + n }, integral)
}