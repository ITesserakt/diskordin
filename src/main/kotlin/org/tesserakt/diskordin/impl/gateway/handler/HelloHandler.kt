package org.tesserakt.diskordin.impl.gateway.handler

import arrow.core.FunctionK
import arrow.fx.typeclasses.Async
import arrow.fx.typeclasses.Dispatchers
import kotlinx.coroutines.delay
import org.tesserakt.diskordin.core.client.EventDispatcher
import org.tesserakt.diskordin.core.data.event.lifecycle.HelloEvent
import org.tesserakt.diskordin.gateway.ForGatewayAPIF
import org.tesserakt.diskordin.gateway.json.commands.Heartbeat
import org.tesserakt.diskordin.gateway.json.commands.Identify
import org.tesserakt.diskordin.gateway.sendPayload

fun <F> EventDispatcher<F>.handleHello(
    token: String,
    sequenceId: () -> Int?,
    compiler: FunctionK<ForGatewayAPIF, F>,
    A: Async<F>,
    D: Dispatchers<F>
) = A.fx.async {
    val event = !subscribeOn<HelloEvent>()
    val interval = event.heartbeatInterval

    !sendPayload(
        Identify(
            token,
            Identify.ConnectionProperties(
                System.getProperty("os.name"),
                "Diskordin",
                "Diskordin"
            ),
            shard = arrayOf(0, 1)
        ),
        sequenceId()
    ).foldMap(compiler, this)

    continueOn(D.io())
    while (true) {
        !sendPayload(Heartbeat(sequenceId()), sequenceId()).foldMap(compiler, this)
        !effect { delay(interval) }
    }
}
