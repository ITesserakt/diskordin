package org.tesserakt.diskordin.gateway.interpreter

import arrow.Kind
import arrow.core.ForListK
import arrow.core.FunctionK
import arrow.core.ListK
import arrow.core.extensions.listk.applicative.just
import arrow.core.k
import io.reactivex.Observable
import org.tesserakt.diskordin.gateway.ForGatewayAPIF
import org.tesserakt.diskordin.gateway.GatewayAPIF
import org.tesserakt.diskordin.gateway.fix

val Implementation.listKInterpreter
    get() = object : FunctionK<ForGatewayAPIF, ForListK> {
        override fun <A> invoke(fa: Kind<ForGatewayAPIF, A>) = when (val op = fa.fix()) {
            is GatewayAPIF.Send -> send(op.data).just()
            is GatewayAPIF.WebSocketEvents -> Observable.fromPublisher(receive())
                .blockingIterable().toList().k()
        } as ListK<A>
    }