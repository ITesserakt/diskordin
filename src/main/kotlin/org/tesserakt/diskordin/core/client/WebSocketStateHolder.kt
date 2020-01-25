package org.tesserakt.diskordin.core.client

import org.tesserakt.diskordin.gateway.json.IToken

abstract class WebSocketStateHolder {
    abstract fun getState(): IToken
    internal abstract fun update(payload: IToken)
    abstract fun observe(block: (old: IToken, new: IToken) -> Unit)
}