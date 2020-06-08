@file:Suppress("NOTHING_TO_INLINE")

package org.tesserakt.diskordin.util.enums

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
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

interface ValuedEnumEq<E, I : Any> : Eq<ValuedEnum<E, I>> where E : Enum<E>, E : IValued<E, I> {
    override fun ValuedEnum<E, I>.eqv(b: ValuedEnum<E, I>): Boolean = this == b
}

inline fun <E, I> ValuedEnum.Companion.eq(): Eq<ValuedEnum<E, I>>
        where E : Enum<E>, I : Any, E : IValued<E, I> = object : ValuedEnumEq<E, I> {}

interface ValuedEnumHash<E, I : Any> : Hash<ValuedEnum<E, I>> where E : Enum<E>, E : IValued<E, I> {
    override fun ValuedEnum<E, I>.hash(): Int = hashCode()
}

inline fun <E, I> ValuedEnum.Companion.hash(): Hash<ValuedEnum<E, I>>
        where E : Enum<E>, I : Any, E : IValued<E, I> =
    object : ValuedEnumHash<E, I>, Eq<ValuedEnum<E, I>> by ValuedEnum.eq() {}