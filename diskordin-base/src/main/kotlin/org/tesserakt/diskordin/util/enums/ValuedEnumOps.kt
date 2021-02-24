@file:Suppress("NOTHING_TO_INLINE")

package org.tesserakt.diskordin.util.enums

import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import org.tesserakt.diskordin.util.typeclass.Integral

interface ValuedEnumSemigroup<E, I : Any> : Semigroup<ValuedEnum<E, I>> where E : Enum<E>, E : IValued<E, I> {
    override fun ValuedEnum<E, I>.combine(b: ValuedEnum<E, I>): ValuedEnum<E, I> = this or b
}

inline fun <E, I> ValuedEnum.Companion.semigroup(): Semigroup<ValuedEnum<E, I>>
        where E : Enum<E>, I : Any, E : IValued<E, I> = object : ValuedEnumSemigroup<E, I> {}

interface ValuedEnumMonoid<E, I : Any> : Monoid<ValuedEnum<E, I>> where E : Enum<E>, E : IValued<E, I>

inline fun <E, I> ValuedEnum.Companion.monoid(integral: Integral<I>): Monoid<ValuedEnum<E, I>>
        where E : Enum<E>, I : Any, E : IValued<E, I> = integral.run {
    object : ValuedEnumMonoid<E, I>, Semigroup<ValuedEnum<E, I>> by ValuedEnum.semigroup() {
        override fun empty(): ValuedEnum<E, I> = none(integral)
    }
}