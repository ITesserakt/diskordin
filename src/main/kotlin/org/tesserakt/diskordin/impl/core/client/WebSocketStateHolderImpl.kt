package org.tesserakt.diskordin.impl.core.client

import arrow.core.left
import arrow.core.right
import org.tesserakt.diskordin.core.client.WebSocketStateHolder
import org.tesserakt.diskordin.gateway.json.IToken
import org.tesserakt.diskordin.gateway.json.Payload
import org.tesserakt.diskordin.gateway.json.token.*
import kotlin.properties.Delegates

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class WebSocketStateHolderImpl : WebSocketStateHolder() {
    @get:JvmName("_getState")
    private var state by Delegates.observable<IToken>(NoConnection) { _, old, new ->
        listeners.forEach { it(old, new) }
    }

    private val listeners = mutableListOf<(IToken, IToken) -> Unit>()

    private fun parseWebSocketState(payload: Payload<in IToken>) = when (payload.name) {
        "CONNECTION_OPENED" -> payload.unwrap<ConnectionOpened>().right()
        "CONNECTION_CLOSING" -> payload.unwrap<ConnectionClosing>().right()
        "CONNECTION_CLOSED" -> payload.unwrap<ConnectionClosed>().right()
        "CONNECTION_FAILED" -> payload.unwrap<ConnectionFailed>().right()
        else -> IllegalStateException("Unknown state").left()
    }

    override fun getState(): IToken = state

    override fun update(payload: Payload<in IToken>) = parseWebSocketState(payload).map {
        if (it is ConnectionClosed || it is ConnectionFailed) {
            state = it
            state = NoConnection
        } else
            state = it
    }

    override fun observe(block: (old: IToken, new: IToken) -> Unit) {
        listeners += block
    }
}