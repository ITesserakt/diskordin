package org.tesserakt.diskordin.util.enums

import org.tesserakt.diskordin.util.typeclass.Integral

class ValuedEnum<E, I>(override val code: I, val integral: Integral<I>) : IValued<E, I>, Integral<I> by integral
        where E : Enum<E>, E : IValued<E, I> {
    operator fun contains(other: IValued<E, I>) = this.code and other.code == other.code
}