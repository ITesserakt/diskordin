package org.tesserakt.diskordin.impl.core.client

import org.tesserakt.diskordin.core.client.WebSocketStateHolder
import org.tesserakt.diskordin.gateway.json.IToken
import org.tesserakt.diskordin.gateway.json.token.NoConnection

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class WebSocketStateHolderImpl : WebSocketStateHolder() {
    private var state: IToken = NoConnection

    override fun getState(): IToken = state

    override fun update(payload: IToken) = payload.let {
        state = it
    }
}