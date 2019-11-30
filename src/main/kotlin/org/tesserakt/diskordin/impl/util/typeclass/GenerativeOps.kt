package org.tesserakt.diskordin.impl.util.typeclass

import arrow.Kind
import arrow.extension
import arrow.fx.rx2.ForFlowableK
import arrow.fx.rx2.ForObservableK
import arrow.fx.rx2.k
import arrow.fx.typeclasses.Proc
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import org.tesserakt.diskordin.util.typeclass.Generative

@extension
interface ObservableGenerative : Generative<ForObservableK> {
    override fun <A> generate(proc: Proc<A>): Kind<ForObservableK, A> = Observable.create<A> { emitter ->
        proc { either ->
            either.fold(
                { emitter.tryOnError(it) },
                { emitter.onNext(it) }
            )
        }
    }.k()
}

@extension
interface FlowableGenerative : Generative<ForFlowableK> {
    override fun <A> generate(
        proc: Proc<A>
    ): Kind<ForFlowableK, A> = Flowable.create<A>({ emitter ->
        proc { either ->
            either.fold(
                { emitter.onError(it) },
                { emitter.onNext(it) }
            )
        }
    }, BackpressureStrategy.BUFFER).k()
}