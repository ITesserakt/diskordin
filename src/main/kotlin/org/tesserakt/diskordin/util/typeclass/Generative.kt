package org.tesserakt.diskordin.util.typeclass

import arrow.Kind
import arrow.fx.typeclasses.Proc

interface Generative<F> {
    fun <A> generate(proc: Proc<A>): Kind<F, A>
}