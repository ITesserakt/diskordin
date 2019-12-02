package org.tesserakt.diskordin.impl.gateway.handler

import arrow.core.FunctionK
import arrow.fx.typeclasses.Async
import org.tesserakt.diskordin.core.client.EventDispatcher
import org.tesserakt.diskordin.core.client.WebSocketStateHolder
import org.tesserakt.diskordin.core.data.event.lifecycle.ReadyEvent
import org.tesserakt.diskordin.gateway.ForGatewayAPIF
import org.tesserakt.diskordin.gateway.json.commands.Resume
import org.tesserakt.diskordin.gateway.json.token.ConnectionClosed
import org.tesserakt.diskordin.gateway.json.token.ConnectionFailed
import org.tesserakt.diskordin.gateway.sendPayload
import org.tesserakt.diskordin.impl.core.client.GlobalGatewayLifecycle

fun <F> EventDispatcher<F>.restartHandler(
    token: String,
    sequenceId: () -> Int?,
    state: WebSocketStateHolder,
    compiler: FunctionK<ForGatewayAPIF, F>,
    A: Async<F>
) = A.fx.async {
    val sessionId = subscribeOn<ReadyEvent>().bind().sessionId

    !effect {
        state.observe { _, it ->
            if (it is ConnectionFailed || it is ConnectionClosed) {
                GlobalGatewayLifecycle.restart()
            }
        }
    }

    !sendPayload(Resume(token, sessionId, sequenceId()), sequenceId()).foldMap(compiler, this)
    Unit
}