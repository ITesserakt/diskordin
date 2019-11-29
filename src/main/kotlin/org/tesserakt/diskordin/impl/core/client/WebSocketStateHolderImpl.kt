package org.tesserakt.diskordin.impl.core.client

import arrow.core.left
import arrow.core.right
import org.tesserakt.diskordin.core.client.WebSocketStateHolder
import org.tesserakt.diskordin.gateway.json.IToken
import org.tesserakt.diskordin.gateway.json.Payload
import org.tesserakt.diskordin.gateway.json.token.*
import java.util.concurrent.atomic.AtomicReference

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
class WebSocketStateHolderImpl : WebSocketStateHolder() {
    private val state = AtomicReference(NoConnection as IToken)

    private fun parseWebSocketState(payload: Payload<in IToken>) = when (payload.name) {
        "CONNECTION_OPENED" -> payload.unwrap<ConnectionOpened>().right()
        "CONNECTION_CLOSING" -> payload.unwrap<ConnectionClosing>().right()
        "CONNECTION_CLOSED" -> payload.unwrap<ConnectionClosed>().right()
        "CONNECTION_FAILED" -> payload.unwrap<ConnectionFailed>().right()
        else -> IllegalStateException("Unknown state").left()
    }

    override fun getState(): IToken = state.get()

    override fun update(payload: Payload<in IToken>) = parseWebSocketState(payload).map {
        state.lazySet(it)
    }
}