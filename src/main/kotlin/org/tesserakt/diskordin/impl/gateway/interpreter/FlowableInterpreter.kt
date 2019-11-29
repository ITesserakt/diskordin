package org.tesserakt.diskordin.impl.gateway.interpreter

import arrow.Kind
import arrow.core.FunctionK
import arrow.fx.rx2.FlowableK
import arrow.fx.rx2.ForFlowableK
import arrow.fx.rx2.extensions.flowablek.applicative.just
import arrow.fx.rx2.k
import io.reactivex.Flowable
import org.tesserakt.diskordin.gateway.ForGatewayAPIF
import org.tesserakt.diskordin.gateway.GatewayAPIF
import org.tesserakt.diskordin.gateway.Implementation
import org.tesserakt.diskordin.gateway.fix

val Implementation.flowableInterpreter
    get() = object : FunctionK<ForGatewayAPIF, ForFlowableK> {
        @Suppress("UNCHECKED_CAST")
        override fun <A> invoke(fa: Kind<ForGatewayAPIF, A>): Kind<ForFlowableK, A> = when (val op = fa.fix()) {
            is GatewayAPIF.Send -> send(op.data).just()
            is GatewayAPIF.WebSocketEvents -> Flowable.fromPublisher(receive()).k()
        } as FlowableK<A>
    }