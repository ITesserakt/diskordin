package org.tesserakt.diskordin.impl.gateway.interpreter

import arrow.Kind
import arrow.core.FunctionK
import arrow.fx.rx2.ForObservableK
import arrow.fx.rx2.ObservableK
import arrow.fx.rx2.extensions.observablek.applicative.just
import arrow.fx.rx2.k
import io.reactivex.Observable
import org.tesserakt.diskordin.gateway.ForGatewayAPIF
import org.tesserakt.diskordin.gateway.GatewayAPIF
import org.tesserakt.diskordin.gateway.Implementation
import org.tesserakt.diskordin.gateway.fix

val Implementation.observableInterpreter
    get() = object : FunctionK<ForGatewayAPIF, ForObservableK> {
        @Suppress("UNCHECKED_CAST")
        override fun <A> invoke(fa: Kind<ForGatewayAPIF, A>): Kind<ForObservableK, A> = when (val op = fa.fix()) {
            is GatewayAPIF.Send -> send(op.data).just()
            GatewayAPIF.WebSocketEvents -> Observable.fromPublisher(receive()).k()
        } as ObservableK<A>
    }