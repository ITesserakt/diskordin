package org.tesserakt.diskordin.impl.gateway.interpreter

import arrow.Kind
import arrow.core.FunctionK
import arrow.fx.rx2.FlowableK
import arrow.fx.rx2.ForFlowableK
import arrow.fx.rx2.extensions.flowablek.applicative.just
import arrow.fx.rx2.extensions.flowablek.effect.effect
import arrow.fx.rx2.extensions.flowablek.monad.flatTap
import arrow.fx.rx2.k
import io.reactivex.Flowable
import org.tesserakt.diskordin.gateway.ForGatewayAPIF
import org.tesserakt.diskordin.gateway.GatewayAPIF
import org.tesserakt.diskordin.gateway.Implementation
import org.tesserakt.diskordin.gateway.fix
import org.tesserakt.diskordin.gateway.json.Opcode

val Implementation.flowableInterpreter
    get() = object : FunctionK<ForGatewayAPIF, ForFlowableK> {
        @Suppress("UNCHECKED_CAST")
        override fun <A> invoke(fa: Kind<ForGatewayAPIF, A>): Kind<ForFlowableK, A> = when (val op = fa.fix()) {
            is GatewayAPIF.Send -> send(op.data).just().flatTap {
                FlowableK.effect().effect {
                    logger.logSend(it, op.data.opcode().takeIf { it != Opcode.DISPATCH }?.name ?: op.data.name!!)
                }
            }
            is GatewayAPIF.WebSocketEvents -> Flowable.fromPublisher(receive()).k()
        } as FlowableK<A>
    }