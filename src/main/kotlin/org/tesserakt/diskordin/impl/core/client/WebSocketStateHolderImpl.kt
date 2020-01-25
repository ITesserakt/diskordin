package org.tesserakt.diskordin.impl.core.client

import org.tesserakt.diskordin.core.client.WebSocketStateHolder
import org.tesserakt.diskordin.gateway.json.IToken
import org.tesserakt.diskordin.gateway.json.token.ConnectionClosed
import org.tesserakt.diskordin.gateway.json.token.ConnectionFailed
import org.tesserakt.diskordin.gateway.json.token.NoConnection
import kotlin.properties.Delegates

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class WebSocketStateHolderImpl : WebSocketStateHolder() {
    @get:JvmName("_getState")
    private var state by Delegates.observable<IToken>(NoConnection) { _, old, new ->
        listeners.forEach { it(old, new) }
    }

    private val listeners = mutableListOf<(IToken, IToken) -> Unit>()

    override fun getState(): IToken = state

    override fun update(payload: IToken) = payload.let {
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