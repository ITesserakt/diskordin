package org.tesserakt.diskordin.impl.util.typeclass

import arrow.Kind
import arrow.extension
import arrow.fx.rx2.ForFlowableK
import arrow.fx.rx2.ForObservableK
import arrow.fx.rx2.extensions.FlowableKAsync
import arrow.fx.rx2.extensions.ObservableKAsync
import arrow.fx.rx2.fix
import arrow.fx.rx2.k
import arrow.fx.typeclasses.Proc
import arrow.fx.typeclasses.ProcF
import io.reactivex.Flowable
import io.reactivex.Observable
import org.tesserakt.diskordin.util.typeclass.Generative

@extension
interface ObservableGenerative : Generative<ForObservableK>, ObservableKAsync {
    override fun <A> generate(proc: Proc<A>): Kind<ForObservableK, A> = Observable.create<A> { emitter ->
        proc { either ->
            either.fold(
                { emitter.tryOnError(it) },
                { emitter.onNext(it) }
            )
        }
    }.k()

    override fun <A> generateF(proc: ProcF<ForObservableK, A>): Kind<ForObservableK, A> =
        Observable.create<A> { emitter ->
            val disposable = proc { either ->
                either.fold(
                    { emitter.tryOnError(it) },
                    { emitter.onNext(it) }
                )
            }.fix().observable.subscribe({}, { emitter.tryOnError(it) })

            emitter.setCancellable(disposable::dispose)
        }.k()
}

@extension
interface FlowableGenerative : Generative<ForFlowableK>, FlowableKAsync {
    override fun <A> generate(
        proc: Proc<A>
    ): Kind<ForFlowableK, A> = Flowable.create<A>({ emitter ->
        proc { either ->
            either.fold(
                { emitter.onError(it) },
                { emitter.onNext(it) }
            )
        }
    }, BS()).k()

    override fun <A> generateF(proc: ProcF<ForFlowableK, A>): Kind<ForFlowableK, A> = Flowable.create<A>({ emitter ->
        val disposable = proc { either ->
            either.fold(
                { emitter.tryOnError(it) },
                { emitter.onNext(it) }
            )
        }.fix().flowable.subscribe({}, { emitter.tryOnError(it) })
        emitter.setCancellable(disposable::dispose)
    }, BS()).k()
}