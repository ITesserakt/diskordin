package org.tesserakt.diskordin.impl.gateway.interpreter

import arrow.Kind
import arrow.core.ForListK
import arrow.core.FunctionK
import arrow.core.ListK
import arrow.core.extensions.listk.applicative.just
import arrow.core.k
import io.reactivex.Observable
import org.tesserakt.diskordin.gateway.ForGatewayAPIF
import org.tesserakt.diskordin.gateway.GatewayAPIF
import org.tesserakt.diskordin.gateway.Implementation
import org.tesserakt.diskordin.gateway.fix
import org.tesserakt.diskordin.util.toJson

val Implementation.listKInterpreter
    get() = object : FunctionK<ForGatewayAPIF, ForListK> {
        @Suppress("UNCHECKED_CAST")
        override fun <A> invoke(fa: Kind<ForGatewayAPIF, A>) = when (val op = fa.fix()) {
            is GatewayAPIF.Send -> send(op.data.toJson()).just()
            is GatewayAPIF.WebSocketEvents -> Observable.fromPublisher(receive())
                .blockingIterable().toList().k()
        } as ListK<A>
    }