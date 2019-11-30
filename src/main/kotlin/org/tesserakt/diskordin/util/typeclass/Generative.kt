package org.tesserakt.diskordin.util.typeclass

import arrow.Kind
import arrow.fx.typeclasses.Async
import arrow.fx.typeclasses.Proc
import arrow.fx.typeclasses.ProcF

interface Generative<F> : Async<F> {
    fun <A> generate(proc: Proc<A>): Kind<F, A>
    fun <A> generateF(proc: ProcF<F, A>): Kind<F, A>
}