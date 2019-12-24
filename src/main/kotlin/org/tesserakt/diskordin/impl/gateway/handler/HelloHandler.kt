package org.tesserakt.diskordin.impl.gateway.handler

import arrow.core.FunctionK
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.milliseconds
import org.tesserakt.diskordin.core.client.EventDispatcher
import org.tesserakt.diskordin.core.client.WebSocketStateHolder
import org.tesserakt.diskordin.core.data.event.lifecycle.HelloEvent
import org.tesserakt.diskordin.gateway.ForGatewayAPIF
import org.tesserakt.diskordin.gateway.json.commands.Heartbeat
import org.tesserakt.diskordin.gateway.json.commands.Identify
import org.tesserakt.diskordin.gateway.json.token.ConnectionOpened
import org.tesserakt.diskordin.gateway.sendPayload

internal fun <F> EventDispatcher<F>.handleHello(
    token: String,
    compress: Boolean,
    webSocketState: WebSocketStateHolder,
    sequenceId: () -> Int?,
    compiler: FunctionK<ForGatewayAPIF, F>,
    CC: Concurrent<F>
) = CC.fx.concurrent {
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
            compress = compress,
            shard = arrayOf(0, 1)
        ),
        sequenceId()
    ).foldMap(compiler, this)

    while (webSocketState.getState() is ConnectionOpened) { //FIXME: cannot send heartbeat if gateway restarts
        !sendPayload(Heartbeat(sequenceId()), sequenceId()).foldMap(compiler, this)
        !sleep(interval.milliseconds)
    }
}
